package edu.festival.deichkind

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import edu.festival.deichkind.models.Report
import edu.festival.deichkind.util.DykeManager

class ReportDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        val report = intent.getBundleExtra("BUNDLE").getParcelable("REPORT") as Report

        val toolbar = findViewById<Toolbar>(R.id.report_detail_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = report.title

        val types = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_type_keys).indices) {
            types[resources.getStringArray(R.array.report_spinner_type_keys)[i]] = resources.getStringArray(R.array.report_spinner_type_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_type).text = types[report.details.type]

        val dyke = DykeManager.getInstance(null).dykes.find { it.id == report.dykeId }
        findViewById<TextView>(R.id.report_detail_dyke).text = "${dyke?.name} (${dyke?.city}, ${dyke?.state})"

        val position = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_position_keys).indices) {
            position[resources.getStringArray(R.array.report_spinner_position_keys)[i]] = resources.getStringArray(R.array.report_spinner_position_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_position).text = position[report.position] ?: report.position

        findViewById<TextView>(R.id.report_detail_location).text = "${report.latitude} / ${report.longitude}"

        val waterLossTypes = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_waterloss_keys).indices) {
            waterLossTypes[resources.getStringArray(R.array.report_spinner_waterloss_keys)[i]] = resources.getStringArray(R.array.report_spinner_waterloss_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_waterloss).text = waterLossTypes[report.details.waterLoss] ?: report.details.waterLoss

        val waterConditionTypes = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_watercondition_keys).indices) {
            waterConditionTypes[resources.getStringArray(R.array.report_spinner_watercondition_keys)[i]] = resources.getStringArray(R.array.report_spinner_watercondition_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_watercondition).text = waterConditionTypes[report.details.waterCondition] ?: report.details.waterCondition

        val leakageTypes = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_leakagetype_keys).indices) {
            leakageTypes[resources.getStringArray(R.array.report_spinner_leakagetype_keys)[i]] = resources.getStringArray(R.array.report_spinner_leakagetype_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_leakagetype).text = leakageTypes[report.details.leakageType] ?: report.details.leakageType

        val deformationTypes = HashMap<String, String>()
        for (i in resources.getStringArray(R.array.report_spinner_deformationtype_keys).indices) {
            deformationTypes[resources.getStringArray(R.array.report_spinner_deformationtype_keys)[i]] = resources.getStringArray(R.array.report_spinner_deformationtype_values)[i]
        }

        findViewById<TextView>(R.id.report_detail_deformationtype).text = deformationTypes[report.details.deformationType] ?: report.details.deformationType
    }
}
