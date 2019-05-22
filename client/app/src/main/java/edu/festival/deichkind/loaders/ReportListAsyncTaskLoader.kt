package edu.festival.deichkind.loaders

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.models.Report
import java.io.File
import java.net.URL

class ReportListAsyncTaskLoader(context: Context) : AsyncTaskLoader<Array<Report>>(context) {

    private var data: Array<Report> = arrayOf()

    override fun loadInBackground(): Array<Report>? {
        val jsonFile = File(context.filesDir, "reports.json")
        var jsonText: String

        jsonText = if (!jsonFile.exists()) {
            URL("https://edu.festival.ml/deichkind/api/reports").readText()
        } else {
            jsonFile.readText()
        }

        if (!jsonFile.exists()) {
            jsonFile.writeText(jsonText)
        }

        data = Gson().fromJson(jsonText, object : TypeToken<Array<Report>>() {}.type)

        return data
    }

    override fun onStartLoading() {
        if (data.isNotEmpty()) {
            deliverResult(data)
        } else {
            forceLoad()
        }
    }

}