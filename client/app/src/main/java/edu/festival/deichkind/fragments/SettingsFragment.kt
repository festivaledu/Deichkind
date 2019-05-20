package edu.festival.deichkind.fragments

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import edu.festival.deichkind.R
import android.content.SharedPreferences
import android.support.v7.preference.ListPreference
import android.widget.Toast


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.app_resources)

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)

        if (preference is ListPreference) {
            preference.summary = preference.entry

            Toast.makeText(activity, "Changed " + key + " to: " + preference.entry, Toast.LENGTH_LONG).show()
        }
    }
}
