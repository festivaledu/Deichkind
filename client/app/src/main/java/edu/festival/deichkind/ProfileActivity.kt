package edu.festival.deichkind


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class ProfileActivity : AppCompatActivity() {

    var apiResponse = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.setTitle(R.string.profile_title)

        findViewById<TextView>(R.id.profile_register_link).apply {
            text = Html.fromHtml("Noch nicht registriert? <a href=\"https://edu.festival.ml/deichkind/#/register\">Jetzt registrieren</a>")
            movementMethod = LinkMovementMethod.getInstance()
        }

        findViewById<Button>(R.id.profile_login_button).setOnClickListener {
            tryLogin("root", "alpine")
            Toast.makeText(this, apiResponse, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.option_logout -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun tryLogin(username: String, password: String) = runBlocking(Dispatchers.Default) {
        val HEX_CHARS = "0123456789abcdef"
        val bytes = MessageDigest
            .getInstance("SHA-512")
            .digest(password.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        val body = "{\"username\": \"" + username + "\",\"password\": \"" + result.toString() + "\"}"

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
                apiResponse = response.toString()
            }
        }
    }
}
