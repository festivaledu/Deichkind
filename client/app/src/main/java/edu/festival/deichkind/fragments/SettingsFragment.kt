package edu.festival.deichkind.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import edu.festival.deichkind.R
import android.content.SharedPreferences
import android.support.v7.preference.ListPreference
import android.widget.Toast
import edu.festival.deichkind.MainActivity
import edu.festival.deichkind.SettingsActivity
import java.io.File


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.app_resources)

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        findPreference("sync-data-now").setOnPreferenceClickListener {
            val reportFile = File(context?.filesDir, "reports.json")

            if (reportFile.exists()) {
                reportFile.delete()
            }

            (activity as SettingsActivity).apply {
                setResult(Activity.RESULT_OK)
                finish()
            }

            true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)

        if (preference is ListPreference) {
            preference.summary = preference.entry
        }
    }
}
