package edu.festival.deichkind.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson

import edu.festival.deichkind.R
import edu.festival.deichkind.models.Dyke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
import com.google.gson.reflect.TypeToken
import edu.festival.deichkind.adapters.DykeListAdapter


class DykeListFragment : Fragment() {

    var dykesResponse: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dyke_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.dyke_list_refresh_button).setOnClickListener {
            tryLoadDykes(view)
        }

        tryLoadDykes(view)
    }

    private fun getDykes() = runBlocking(Dispatchers.Default) {
        launch {
            dykesResponse = URL("https://test.festival.ml/deichkind/api/dykes").readText()
        }
    }

    private fun tryLoadDykes(view: View) {
        val networkInfo = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            getDykes()

            val dykes: Array<Dyke> = Gson().fromJson(dykesResponse, object : TypeToken<Array<Dyke>>() {}.type)
            val recyclerView = view.findViewById<RecyclerView>(R.id.dyke_list_recycler)

            view.findViewById<LinearLayout>(R.id.dyke_list_offline_note).visibility = View.GONE

            recyclerView.apply {
                layoutManager = LinearLayoutManager(this@DykeListFragment.context)
                adapter = DykeListAdapter(dykes)
            }
        } else {
            view.findViewById<LinearLayout>(R.id.dyke_list_offline_note).visibility = View.VISIBLE
        }
    }

}
