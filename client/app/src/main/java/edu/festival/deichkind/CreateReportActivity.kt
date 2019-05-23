package edu.festival.deichkind

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import edu.festival.deichkind.listeners.OnItemSelectedListener

class CreateReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_report)

        val toolbar = findViewById<Toolbar>(R.id.create_report_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.create_report_title)

        val typeSpinner = findViewById<Spinner>(R.id.create_report_type_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_type_keys)[0],
                resources.getStringArray(R.array.report_spinner_type_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_type_keys)[1],
                resources.getStringArray(R.array.report_spinner_type_values)[1]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            typeSpinner.adapter = it
        }

        val waterLossSpinner = findViewById<Spinner>(R.id.create_report_waterloss_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_waterloss_keys)[0],
                resources.getStringArray(R.array.report_spinner_waterloss_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_waterloss_keys)[1],
                resources.getStringArray(R.array.report_spinner_waterloss_values)[1]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_waterloss_keys)[2],
                resources.getStringArray(R.array.report_spinner_waterloss_values)[2]
            ),
            SpinnerItem(
                "other",
                resources.getStringArray(R.array.report_spinner_waterloss_values)[3]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            waterLossSpinner.adapter = it
        }
        waterLossSpinner.onItemSelectedListener = object : OnItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerItem = waterLossSpinner.selectedItem as SpinnerItem
                findViewById<EditText>(R.id.create_report_waterloss_input).visibility = if (spinnerItem.getKey() == "other") View.VISIBLE else View.GONE
            }
        }

        val waterConditionSpinner = findViewById<Spinner>(R.id.create_report_watercondition_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_watercondition_keys)[0],
                resources.getStringArray(R.array.report_spinner_watercondition_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_watercondition_keys)[1],
                resources.getStringArray(R.array.report_spinner_watercondition_values)[1]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_watercondition_keys)[2],
                resources.getStringArray(R.array.report_spinner_watercondition_values)[2]
            ),
            SpinnerItem(
                "other",
                resources.getStringArray(R.array.report_spinner_watercondition_values)[3]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            waterConditionSpinner.adapter = it
        }
        waterConditionSpinner.onItemSelectedListener = object : OnItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerItem = waterConditionSpinner.selectedItem as SpinnerItem
                findViewById<EditText>(R.id.create_report_watercondition_input).visibility = if (spinnerItem.getKey() == "other") View.VISIBLE else View.GONE
            }
        }

        val leakageTypeSpinner = findViewById<Spinner>(R.id.create_report_leakagetype_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_leakagetype_keys)[0],
                resources.getStringArray(R.array.report_spinner_leakagetype_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_leakagetype_keys)[1],
                resources.getStringArray(R.array.report_spinner_leakagetype_values)[1]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_leakagetype_keys)[2],
                resources.getStringArray(R.array.report_spinner_leakagetype_values)[2]
            ),
            SpinnerItem(
                "other",
                resources.getStringArray(R.array.report_spinner_leakagetype_values)[3]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            leakageTypeSpinner.adapter = it
        }
        leakageTypeSpinner.onItemSelectedListener = object : OnItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerItem = leakageTypeSpinner.selectedItem as SpinnerItem
                findViewById<EditText>(R.id.create_report_leakagetype_input).visibility = if (spinnerItem.getKey() == "other") View.VISIBLE else View.GONE
            }
        }

        val deformationTypeSpinner = findViewById<Spinner>(R.id.create_report_deformationtype_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_deformationtype_keys)[0],
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_deformationtype_keys)[1],
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[1]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_deformationtype_keys)[2],
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[2]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_deformationtype_keys)[3],
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[3]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_deformationtype_keys)[4],
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[4]
            ),
            SpinnerItem(
                "other",
                resources.getStringArray(R.array.report_spinner_deformationtype_values)[5]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            deformationTypeSpinner.adapter = it
        }
        deformationTypeSpinner.onItemSelectedListener = object : OnItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerItem = deformationTypeSpinner.selectedItem as SpinnerItem
                findViewById<EditText>(R.id.create_report_deformationtype_input).visibility = if (spinnerItem.getKey() == "other") View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_report_options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.option_close_create_report -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
