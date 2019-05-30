package edu.festival.deichkind


import android.graphics.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.models.AuthResponse
import edu.festival.deichkind.models.ProfileResponse
import edu.festival.deichkind.models.Session
import edu.festival.deichkind.util.BitmapHelper
import edu.festival.deichkind.util.SessionManager
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest


class ProfileActivity : AppCompatActivity() {

    var optionsMenu: Menu? = null

    fun composeHash(input: ByteArray): String {
        val characters = "0123456789abcdef"
        val bytes = MessageDigest.getInstance("SHA-512").digest(input)
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(characters[i shr 4 and 0x0f])
            result.append(characters[i and 0x0f])
        }

        return result.toString()
    }

    private fun login(username: String, password: String) = GlobalScope.launch {
        val result = tryLoginAsync(username, composeHash(password.toByteArray())).await()

        if (result.statusCode == 200) {
            val authResponse: AuthResponse = Gson().fromJson(result.rawResult, object : TypeToken<AuthResponse>() {}.type)

            if (SessionManager.getInstance(null).session == null) {
                SessionManager.getInstance(null).session = Session()
            }

            SessionManager.getInstance(null).session?.apply {
                authToken = authResponse.token
                refreshToken = authResponse.refreshToken
            }

            val profile = tryGetProfileAsync().await()

            SessionManager.getInstance(null).session?.apply {
                id = profile.id
                this.username = profile.username
                email = profile.email
            }

            SessionManager.getInstance(null).session?.avatar = BitmapHelper().getCircularBitmap(BitmapFactory.decodeStream(URL("https://edu.festival.ml/deichkind/api/account/" + SessionManager.getInstance(null).session?.id + "/avatar").openConnection().getInputStream()))

            val sessionFile = File(filesDir, "session.json")
            sessionFile.writeText(Gson().toJson(SessionManager.getInstance(null).session))
        }

        runOnUiThread {
            when (result.statusCode) {
                200 -> {
                    Toast.makeText(this@ProfileActivity, getText(R.string.profile_login_successful_notice), Toast.LENGTH_SHORT).show()

                    showProfile()
                }
                401 -> {
                    Toast.makeText(this@ProfileActivity, getText(R.string.profile_password_incorrect_notice), Toast.LENGTH_SHORT).show()
                }
                404 -> {
                    Toast.makeText(this@ProfileActivity, getText(R.string.profile_user_unknown_notice), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@ProfileActivity, getText(R.string.profile_unknown_error_notice), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setTitle(R.string.profile_title)

        if (SessionManager.getInstance(null).session != null) {
            showProfile()
        } else {
            val usernameInput = findViewById<EditText>(R.id.profile_username)
            val passwordInput = findViewById<EditText>(R.id.profile_password)

            findViewById<TextView>(R.id.profile_register_link).apply {
                text =
                    Html.fromHtml("Noch nicht registriert? <a href=\"https://edu.festival.ml/deichkind/#/register\">Jetzt registrieren</a>")
                movementMethod = LinkMovementMethod.getInstance()
            }

            findViewById<Button>(R.id.profile_login_button).setOnClickListener {
                val username = usernameInput.text
                val password = passwordInput.text

                if (username.isEmpty() || username.isBlank() || password.isEmpty() || password.isBlank()) {
                    Toast.makeText(this, getString(R.string.profile_empty_fields_notice), Toast.LENGTH_SHORT).show()
                } else {
                    GlobalScope.launch {
                        login(username.toString(), password.toString())
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_options, menu)

        optionsMenu = menu

        if (SessionManager.getInstance(null).session != null) {
            menu?.findItem(R.id.option_logout)?.isVisible = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.option_logout -> {
            SessionManager.getInstance(null).session = null

            val sessionFile = File(filesDir, "session.json")
            if (sessionFile.exists()) {
                sessionFile.delete()
            }

            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showProfile() {
        optionsMenu?.findItem(R.id.option_logout)?.isVisible = true
        findViewById<LinearLayout>(R.id.profile_head).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.profile_subbar).visibility = View.GONE
        findViewById<LinearLayout>(R.id.profile_inputs).visibility = View.GONE

        findViewById<TextView>(R.id.profile_my_username).text = SessionManager.getInstance(null).session?.username
        findViewById<TextView>(R.id.profile_my_email).text = SessionManager.getInstance(null).session?.email

        findViewById<ImageView>(R.id.profile_my_avatar).setImageBitmap(SessionManager.getInstance(null).session?.avatar)
    }

    private fun tryGetProfileAsync(): Deferred<ProfileResponse> = GlobalScope.async {
        var result: ProfileResponse

        URL("https://edu.festival.ml/deichkind/api/account/me").openConnection().let {
            it as HttpURLConnection
        }.apply {
            setRequestProperty("Authorization", "Bearer " + SessionManager.getInstance(null).session?.authToken)
            requestMethod = "GET"
        }.let {
            if (it.responseCode == 200) it.inputStream else it.errorStream
        }.let { streamToRead ->
            BufferedReader(InputStreamReader(streamToRead)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }

                it.close()

                result = Gson().fromJson(response.toString(), object : TypeToken<ProfileResponse>() {}.type)

                return@async result
            }
        }
    }

    private fun tryLoginAsync(username: String, password: String): Deferred<ApiResult> = GlobalScope.async {
        val body = "{\"username\": \"$username\",\"password\": \"$password\"}"
        val result = ApiResult()

        URL("https://edu.festival.ml/deichkind/api/auth/login").openConnection().let {
            it as HttpURLConnection
        }.apply {
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            requestMethod = "POST"
            doOutput = true

            val outputWriter = OutputStreamWriter(outputStream)
            outputWriter.write(body)
            outputWriter.flush()
        }.let {
            result.statusCode = it.responseCode

            if (it.responseCode == 200) {
                it.inputStream
            } else {
                it.errorStream
            }
        }.let { streamToRead ->
            BufferedReader(InputStreamReader(streamToRead)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }

                it.close()

                result.rawResult = response.toString()

                return@async result
            }
        }
    }
}
