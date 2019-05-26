package edu.festival.deichkind

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import edu.festival.deichkind.fragments.MainFragment
import edu.festival.deichkind.fragments.ReportListFragment
import edu.festival.deichkind.util.SessionManager

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var drawerLayout: DrawerLayout? = null

    var reportListFragment: ReportListFragment? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            forceReloadLoaders()
            Toast.makeText(this, "Sync in progress...", Toast.LENGTH_SHORT).show()
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
                startActivity(Intent(this, ProfileActivity::class.java))
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

    fun forceReloadLoaders() {
        reportListFragment?.forceReloadLoader()
    }
}
