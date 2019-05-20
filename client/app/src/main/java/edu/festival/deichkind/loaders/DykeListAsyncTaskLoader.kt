package edu.festival.deichkind.loaders

import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.models.Dyke
import java.io.File
import java.net.URL

class DykeListAsyncTaskLoader(context: Context) : AsyncTaskLoader<Array<Dyke>>(context) {

    private var data: Array<Dyke> = arrayOf()

    override fun loadInBackground(): Array<Dyke>? {
        val jsonFile = File(context.filesDir, "dykes.json")
        var jsonText: String

        jsonText = if (!jsonFile.exists()) {
            URL("https://edu.festival.ml/deichkind/api/dykes").readText()
        } else {
            jsonFile.readText()
        }

        if (!jsonFile.exists()) {
            jsonFile.writeText(jsonText)
        }

        data = Gson().fromJson(jsonText, object : TypeToken<Array<Dyke>>() {}.type)

        return data
    }

    override fun onStartLoading() {
        if (!data.isEmpty()) {
            deliverResult(data)
        } else {
            forceLoad()
        }
    }

}