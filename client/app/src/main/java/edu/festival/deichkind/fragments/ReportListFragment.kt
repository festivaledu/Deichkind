package edu.festival.deichkind.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import edu.festival.deichkind.CreateReportActivity
import edu.festival.deichkind.R
import edu.festival.deichkind.adapters.ReportListAdapter
import edu.festival.deichkind.loaders.ReportListAsyncTaskLoader
import edu.festival.deichkind.models.Report

class ReportListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.report_list_recycler)

        val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Array<Report>> {
            override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<Report>> {
                return ReportListAsyncTaskLoader(this@ReportListFragment.context as Context)
            }

            override fun onLoadFinished(p0: Loader<Array<Report>>, p1: Array<Report>?) {
                val reportListAdapter = ReportListAdapter(p1 as Array<Report>)
                reportListAdapter.onItemClick = { report -> onItemClick(report) }

                recyclerView.apply {

                    layoutManager = LinearLayoutManager(context)
                    adapter = reportListAdapter
                }
            }

            override fun onLoaderReset(p0: Loader<Array<Report>>) {

            }
        }

        loaderManager.initLoader(0, null, loaderCallbacks)

        view.findViewById<FloatingActionButton>(R.id.report_list_fab).setOnClickListener {
            startActivity(Intent(activity, CreateReportActivity::class.java))
        }
    }

    fun onItemClick(item: Report) {
        Toast.makeText(context, item.title, Toast.LENGTH_LONG).show()
    }

}
