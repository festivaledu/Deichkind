package edu.festival.deichkind


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.profile_title)
    }

}
