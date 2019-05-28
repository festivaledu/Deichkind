package edu.festival.deichkind.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import edu.festival.deichkind.R
import android.content.SharedPreferences
import android.support.v7.preference.ListPreference
import edu.festival.deichkind.SettingsActivity
import edu.festival.deichkind.util.DykeManager
import java.io.File

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        preferenceManager.sharedPreferencesName = "ml.festival.edu.deichkind"

        addPreferencesFromResource(R.xml.app_resources)

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        findPreference("sync-data-now").setOnPreferenceClickListener {
            val dykesFile = File(context?.filesDir, "dykes.json")
            val reportsFile = File(context?.filesDir, "reports.json")

            if (dykesFile.exists()) {
                dykesFile.delete()
            }

            if (reportsFile.exists()) {
                reportsFile.delete()
            }

            (activity as SettingsActivity).apply {
                setResult(Activity.RESULT_OK)
                finish()
            }

            true
        }

        val defaultDykePref = findPreference("default-dyke") as ListPreference
        if (DykeManager.getInstance(null).dykes.isNotEmpty()) {
            defaultDykePref.isEnabled = true

            val entries: MutableList<CharSequence> = mutableListOf()
            val entryValues: MutableList<CharSequence> = mutableListOf()

            DykeManager.getInstance(null).dykes.forEach {
                entries.add("${it.name} (${it.city}, ${it.state})")
                entryValues.add(it.id)
            }

            defaultDykePref.entries = entries.toTypedArray()
            defaultDykePref.entryValues = entryValues.toTypedArray()
        } else {
            defaultDykePref.isEnabled = false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)

        if (preference is ListPreference) {
            preference.summary = preference.entry
        }
    }
}
