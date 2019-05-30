package edu.festival.deichkind

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.fragments.DykeListFragment
import edu.festival.deichkind.fragments.MainFragment
import edu.festival.deichkind.fragments.ReportListFragment
import edu.festival.deichkind.models.RefreshTokenResult
import edu.festival.deichkind.models.Session
import edu.festival.deichkind.models.VerifyResponse
import edu.festival.deichkind.util.BitmapHelper
import edu.festival.deichkind.util.SessionManager
import kotlinx.android.synthetic.main.activity_create_report.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawerLayout: DrawerLayout? = null

    var dykeListFragment: DykeListFragment? = null
    var reportListFragment: ReportListFragment? = null

    private fun checkSession() {
        if (SessionManager.getInstance(null).session != null) {
            drawerLayout?.findViewById<ImageView>(R.id.main_drawer_avatar)?.setImageBitmap(Bitmap.createScaledBitmap(SessionManager.getInstance(null).session?.avatar as Bitmap, 192, 192, false))
            drawerLayout?.findViewById<TextView>(R.id.main_drawer_username)?.text = SessionManager.getInstance(null).session?.username
            drawerLayout?.findViewById<TextView>(R.id.main_drawer_email)?.text = SessionManager.getInstance(null).session?.email

            drawerLayout?.findViewById<LinearLayout>(R.id.main_drawer_profile)?.visibility = View.VISIBLE
            drawerLayout?.findViewById<LinearLayout>(R.id.main_drawer_anonymous)?.visibility = View.GONE
        } else {
            drawerLayout?.findViewById<LinearLayout>(R.id.main_drawer_profile)?.visibility = View.GONE
            drawerLayout?.findViewById<LinearLayout>(R.id.main_drawer_anonymous)?.visibility = View.VISIBLE
        }
    }

    private fun forceReloadLoaders() {
        dykeListFragment?.forceReloadLoader()
        reportListFragment?.forceReloadLoader()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            forceReloadLoaders()
            Toast.makeText(this, getString(R.string.main_sync_in_progress_notice), Toast.LENGTH_SHORT).show()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, MainFragment()).commit()
        }

        drawerLayout = findViewById(R.id.main_drawer)
        val navigationView = findViewById<NavigationView>(R.id.main_navigation_view)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.main_drawer_open_description, R.string.main_drawer_close_description)

        drawerLayout?.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        val sharedPrefs = getSharedPreferences("ml.festival.edu.deichkind", Context.MODE_PRIVATE)
        val syncOnStartup = sharedPrefs.getBoolean("sync-data-on-startup-if-network-available", false)
        if (syncOnStartup) {
            val networkInfo = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                val dykesFile = File(filesDir, "dykes.json")
                val reportsFile = File(filesDir, "reports.json")

                if (dykesFile.exists()) {
                    dykesFile.delete()
                }

                if (reportsFile.exists()) {
                    reportsFile.delete()
                }
            }
        }

        val sessionFile = File(filesDir, "session.json")
        if (sessionFile.exists()) {
            SessionManager.getInstance(null).session = Gson().fromJson(sessionFile.readText(), object : TypeToken<Session>() {}.type)

            GlobalScope.launch {
                val verifyResponse = tryVerifyAsync().await()

                if (verifyResponse.auth) {
                    SessionManager.getInstance(null).session?.authToken = verifyResponse.token
                } else {
                    val result = tryRefreshAsync(SessionManager.getInstance(null).session?.refreshToken as String).await()

                    if (result.statusCode == 200) {
                        val response: RefreshTokenResult = Gson().fromJson(result.rawResult, object : TypeToken<RefreshTokenResult>() {}.type)
                        SessionManager.getInstance(null).session?.authToken = response.token
                    } else {
                        sessionFile.delete()
                        SessionManager.getInstance(null).session = null
                    }
                }

                SessionManager.getInstance(null).session?.avatar = BitmapHelper().getCircularBitmap(BitmapFactory.decodeStream(URL("https://edu.festival.ml/deichkind/api/account/" + SessionManager.getInstance(null).session?.id + "/avatar").openConnection().getInputStream()))

                runOnUiThread {
                    checkSession()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_reports -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, MainFragment()).commit()
                supportActionBar?.setTitle(R.string.app_name)
            }
            R.id.nav_dykes -> {
                val fragment = MainFragment()
                val bundle = Bundle()

                bundle.putInt("TAB_TO_OPEN", 1)
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, fragment).commit()
                supportActionBar?.setTitle(R.string.app_name)
            }
            R.id.nav_profile -> {
                val networkInfo = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

                if (networkInfo != null && networkInfo.isConnected) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else {
                    Toast.makeText(this, getString(R.string.main_network_connection_required_notice), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_settings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
            }
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer)
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onResume() {
        super.onResume()

        checkSession()
    }

    private fun tryRefreshAsync(refreshToken: String): Deferred<ApiResult> = GlobalScope.async {
        val body = "{\"token\": \"$refreshToken\"}"
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

    private fun tryVerifyAsync(): Deferred<VerifyResponse> = GlobalScope.async {
        var result: VerifyResponse

        URL("https://edu.festival.ml/deichkind/api/auth/verify").openConnection().let {
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

                result = Gson().fromJson(response.toString(), object : TypeToken<VerifyResponse>() {}.type)

                return@async result
            }
        }
    }
}
