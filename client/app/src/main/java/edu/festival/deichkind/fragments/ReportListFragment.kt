package edu.festival.deichkind.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import edu.festival.deichkind.MainActivity
import edu.festival.deichkind.R
import edu.festival.deichkind.ReportDetail
import edu.festival.deichkind.adapters.ReportListAdapter
import edu.festival.deichkind.loaders.ReportListAsyncTaskLoader
import edu.festival.deichkind.models.Report
import edu.festival.deichkind.util.SessionManager

class ReportListFragment : Fragment() {

    private var loaderCallbacks: LoaderManager.LoaderCallbacks<Array<Report>>? = null

    fun forceReloadLoader() {
        loaderManager.restartLoader(0, null, loaderCallbacks as LoaderManager.LoaderCallbacks<Array<Report>>)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).reportListFragment = this

        return inflater.inflate(R.layout.fragment_report_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.report_list_recycler)

        loaderCallbacks = object : LoaderManager.LoaderCallbacks<Array<Report>> {
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

        loaderManager.initLoader(0, null, loaderCallbacks as LoaderManager.LoaderCallbacks<Array<Report>>)

        view.findViewById<FloatingActionButton>(R.id.report_list_fab).setOnClickListener {
            if (SessionManager.getInstance(null).session == null) {
                NoSessionDialog().show(fragmentManager, "dialog")
            } else {
                startActivity(Intent(activity, CreateReportActivity::class.java))
            }
        }
    }

    fun onItemClick(item: Report) {
        var bundle = Bundle().apply {
            putParcelable("REPORT", item)
        }

        startActivity(Intent(context, ReportDetail::class.java).apply {
            putExtra("BUNDLE", bundle)
        })
    }
}
