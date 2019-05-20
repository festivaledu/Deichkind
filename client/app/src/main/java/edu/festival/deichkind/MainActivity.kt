package edu.festival.deichkind

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import edu.festival.deichkind.fragments.MainFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, MainFragment()).commit()
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer)
        val navigationView = findViewById<NavigationView>(R.id.main_navigation_view)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.main_drawer_open_description, R.string.main_drawer_close_description);

        drawerLayout.addDrawerListener(drawerToggle)
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
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer)
        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

}
