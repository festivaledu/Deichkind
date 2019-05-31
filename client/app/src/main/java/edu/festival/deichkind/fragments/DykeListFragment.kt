package edu.festival.deichkind.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
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
import edu.festival.deichkind.MainActivity
import edu.festival.deichkind.adapters.DykeListAdapter
import edu.festival.deichkind.loaders.DykeListAsyncTaskLoader
import java.io.File

class DykeListFragment : Fragment() {

    private var loaderCallbacks: LoaderManager.LoaderCallbacks<Array<Dyke>>? = null

    fun forceReloadLoader() {
        loaderManager.restartLoader(0, null, loaderCallbacks as LoaderManager.LoaderCallbacks<Array<Dyke>>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).dykeListFragment = this

        return inflater.inflate(R.layout.fragment_dyke_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* view.findViewById<Button>(R.id.dyke_list_refresh_button).setOnClickListener {
            tryLoadDykes(view)
        }

        tryLoadDykes(view) */


        val recyclerView = view.findViewById<RecyclerView>(R.id.dyke_list_recycler)

        loaderCallbacks = object : LoaderManager.LoaderCallbacks<Array<Dyke>> {
            override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<Dyke>> {
                return DykeListAsyncTaskLoader(this@DykeListFragment.context as Context)
            }

            override fun onLoadFinished(p0: Loader<Array<Dyke>>, p1: Array<Dyke>?) {
                view.findViewById<LinearLayout>(R.id.dyke_list_offline_note).visibility = View.GONE

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = DykeListAdapter(p1 as Array<Dyke>)
                }
            }

            override fun onLoaderReset(p0: Loader<Array<Dyke>>) {

            }
        }

        val networkInfo = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        if (File(context?.filesDir, "dykes.json").exists() || (networkInfo != null && networkInfo.isConnected)) {
            loaderManager.initLoader(0, null, loaderCallbacks as LoaderManager.LoaderCallbacks<Array<Dyke>>)
        }
    }

    /* private fun getDykes() = runBlocking(Dispatchers.Default) {
        dykesResponse = URL("https://edu.festival.ml/deichkind/api/dykes").readText()
    }

    private fun tryLoadDykes(view: View) = runBlocking(Dispatchers.Default) {
        val networkInfo = (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            launch {
                getDykes()

                val dykes: Array<Dyke> = Gson().fromJson(dykesResponse, object : TypeToken<Array<Dyke>>() {}.type)
                val recyclerView = view.findViewById<RecyclerView>(R.id.dyke_list_recycler)

                view.findViewById<LinearLayout>(R.id.dyke_list_offline_note).visibility = View.GONE

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(this@DykeListFragment.context)
                    adapter = DykeListAdapter(dykes)
                }
            }
        } else {
            view.findViewById<LinearLayout>(R.id.dyke_list_offline_note).visibility = View.VISIBLE
        }
    } */

}
