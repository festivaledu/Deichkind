package edu.festival.deichkind

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import edu.festival.deichkind.listeners.OnItemSelectedListener
import edu.festival.deichkind.util.DykeManager
import java.net.HttpURLConnection
import java.net.URL
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import edu.festival.deichkind.models.ReportBlueprint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class CreateReportActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var location: Location? = null

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                findViewById<TextView>(R.id.create_report_latitude).text = location?.latitude.toString()
                findViewById<TextView>(R.id.create_report_longitude).text = location?.longitude.toString()

                this.location = location
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.create_report_image_preview).setImageBitmap(imageBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_report)

        val toolbar = findViewById<Toolbar>(R.id.create_report_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.create_report_title)

        findViewById<FloatingActionButton>(R.id.create_report_fab).setOnClickListener {
            val dykeId = (findViewById<Spinner>(R.id.create_report_dyke_spinner).selectedItem as SpinnerItem).getKey()

            Toast.makeText(this, dykeId, Toast.LENGTH_SHORT).show()

            GlobalScope.launch {
                sendReport(dykeId, ReportBlueprint().apply {
                    title = "Test"
                })
            }
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            getLocation()
        }


        // val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        // mapFragment.getMapAsync(this)


        val dykeEntries: MutableList<SpinnerItem> = DykeManager.getInstance(null).dykes.map {
            SpinnerItem(it.id, it.name)
        } as MutableList<SpinnerItem>


        val dykeSpinner = findViewById<Spinner>(R.id.create_report_dyke_spinner)
        ArrayAdapter(this, R.layout.spinner_item, dykeEntries).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            dykeSpinner.adapter = it
        }

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

    override fun onMapReady(map: GoogleMap?) {
        map?.addMarker(MarkerOptions().position(LatLng(location?.latitude as Double, location?.longitude as Double)))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.option_take_photo -> {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, 1)
                }
            }
            true
        }
        R.id.option_close_create_report -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // related task you need to do.
                    getLocation()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private suspend fun sendReport(dykeId: String, blueprint: ReportBlueprint) {
        val result = trySendReportAsync(dykeId, blueprint).await()

        runOnUiThread {
            when (result.statusCode) {
                200 -> {
                    finish()
                }
                else -> {
                    Toast.makeText(this@CreateReportActivity, "The report couldn't be made", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun trySendReportAsync(dykeId: String, blueprint: ReportBlueprint): Deferred<ApiResult> = GlobalScope.async {
        val body = Gson().toJson(blueprint)
        val result = ApiResult()

        URL("https://edu.festival.ml/deichkind/api/dykes/$dykeId/reports/new").openConnection().let {
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
}
