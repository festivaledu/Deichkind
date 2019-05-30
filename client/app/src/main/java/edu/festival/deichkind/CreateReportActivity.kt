package edu.festival.deichkind

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.Toolbar
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.models.CreateReportResponse
import edu.festival.deichkind.models.ReportBlueprint
import edu.festival.deichkind.util.FormDataHelper
import edu.festival.deichkind.util.SessionManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CreateReportActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentPhotoPath: String = ""
    private var location: Location? = null

    fun compressBitmap(file: File): File? {
        try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image

            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val requiredSize = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= requiredSize && o.outHeight / scale / 2 >= requiredSize) {
                scale *= 2
            }

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)

            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)

            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            return file
        } catch (e: Exception) {
            return null
        }

    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(Date())
        val storageDir: File = cacheDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    findViewById<TextView>(R.id.create_report_latitude).text = location.latitude.toString()
                    findViewById<TextView>(R.id.create_report_longitude).text = location.longitude.toString()
                }

                this.location = location
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
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
            if (findViewById<EditText>(R.id.create_report_title_input).text.isEmpty()) {
                Toast.makeText(this, getString(R.string.create_report_title_missing_notice), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, getString(R.string.create_report_upload_notice), Toast.LENGTH_LONG).show()

            val title = findViewById<EditText>(R.id.create_report_title_input).text.toString()
            val type = (findViewById<Spinner>(R.id.create_report_type_spinner).selectedItem as SpinnerItem).getKey()
            val dykeId = (findViewById<Spinner>(R.id.create_report_dyke_spinner).selectedItem as SpinnerItem).getKey()
            val position = (findViewById<Spinner>(R.id.create_report_position_spinner).selectedItem as SpinnerItem).getKey()
            val positionText = findViewById<EditText>(R.id.create_report_position_input).text.toString()
            val waterLoss = (findViewById<Spinner>(R.id.create_report_waterloss_spinner).selectedItem as SpinnerItem).getKey()
            val waterLossText = findViewById<EditText>(R.id.create_report_waterloss_input).text.toString()
            val waterCondition = (findViewById<Spinner>(R.id.create_report_watercondition_spinner).selectedItem as SpinnerItem).getKey()
            val waterConditionText = findViewById<EditText>(R.id.create_report_watercondition_input).text.toString()
            val leakage = (findViewById<Spinner>(R.id.create_report_leakagetype_spinner).selectedItem as SpinnerItem).getKey()
            val leakageText = findViewById<EditText>(R.id.create_report_leakagetype_input).text.toString()
            val deformation = (findViewById<Spinner>(R.id.create_report_deformationtype_spinner).selectedItem as SpinnerItem).getKey()
            val deformationText = findViewById<EditText>(R.id.create_report_deformationtype_input).text.toString()

            GlobalScope.launch {
                sendReport(dykeId, ReportBlueprint().apply {
                    this.title = title
                    this.message = ""
                    this.latitude = if (this@CreateReportActivity.location != null) this@CreateReportActivity.location?.latitude as Double else 0.0
                    this.longitude = if (this@CreateReportActivity.location != null) this@CreateReportActivity.location?.longitude as Double else 0.0
                    this.position = if (position != "other") position else positionText

                    details.apply {
                        this.type = type
                        this.waterLoss = if (waterLoss != "other") waterLoss else waterLossText
                        this.waterCondition = if (waterCondition != "other") waterCondition else waterConditionText
                        this.leakageType = if (leakage != "other") leakage else leakageText
                        this.deformationType = if (deformation != "other") deformation else deformationText
                    }
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
            SpinnerItem(it.id, "${it.name} (${it.city}, ${it.state})")
        } as MutableList<SpinnerItem>


        val dykeSpinner = findViewById<Spinner>(R.id.create_report_dyke_spinner)
        ArrayAdapter(this, R.layout.spinner_item, dykeEntries).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            dykeSpinner.adapter = it
        }

        val positionSpinner = findViewById<Spinner>(R.id.create_report_position_spinner)
        ArrayAdapter(this, R.layout.spinner_item, arrayOf(
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[0],
                resources.getStringArray(R.array.report_spinner_position_values)[0]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[1],
                resources.getStringArray(R.array.report_spinner_position_values)[1]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[2],
                resources.getStringArray(R.array.report_spinner_position_values)[2]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[3],
                resources.getStringArray(R.array.report_spinner_position_values)[3]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[4],
                resources.getStringArray(R.array.report_spinner_position_values)[4]
            ),
            SpinnerItem(
                resources.getStringArray(R.array.report_spinner_position_keys)[5],
                resources.getStringArray(R.array.report_spinner_position_values)[5]
            ),
            SpinnerItem(
                "other",
                resources.getStringArray(R.array.report_spinner_position_values)[6]
            )
        )).also {
            it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            positionSpinner.adapter = it
        }
        positionSpinner.onItemSelectedListener = object : OnItemSelectedListener() {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerItem = positionSpinner.selectedItem as SpinnerItem
                findViewById<EditText>(R.id.create_report_position_input).visibility = if (spinnerItem.getKey() == "other") View.VISIBLE else View.GONE
            }
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

        val sharedPrefs = getSharedPreferences("ml.festival.edu.deichkind", Context.MODE_PRIVATE)
        val defaultDyke = sharedPrefs.getString("default-dyke", "")

        findViewById<Spinner>(R.id.create_report_dyke_spinner).setSelection(DykeManager.getInstance(null).dykes.indexOfFirst { it.id == defaultDyke })
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
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "edu.festival.deichkind.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 1)
                    }
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
        val report: CreateReportResponse = Gson().fromJson(result.rawResult, object : TypeToken<CreateReportResponse>() {}.type)

        if (result.statusCode == 200 && currentPhotoPath.isNotEmpty()) {
            compressBitmap(File(currentPhotoPath))

            FormDataHelper().multipartRequest("https://edu.festival.ml/deichkind/api/reports/${report.report?.id}/photos", mapOf(), currentPhotoPath,"photo", "image/jpeg")

            File(currentPhotoPath).delete()
        }

        runOnUiThread {
            when (result.statusCode) {
                200 -> {
                    Toast.makeText(this, getString(R.string.create_report_finished_notice), Toast.LENGTH_LONG).show()

                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else -> {
                    Toast.makeText(this@CreateReportActivity, result.rawResult, Toast.LENGTH_SHORT).show()
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
            setRequestProperty("Authorization", "Bearer " + SessionManager.getInstance(null).session?.authToken)
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
