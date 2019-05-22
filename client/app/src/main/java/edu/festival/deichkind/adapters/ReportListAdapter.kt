package edu.festival.deichkind.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import edu.festival.deichkind.R
import edu.festival.deichkind.models.Report
import java.text.DateFormat

class ReportListAdapter(val reports: Array<Report>) : RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder>() {

    class ReportListViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun getItemCount() = reports.size

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        holder.linearLayout.findViewById<TextView>(R.id.report_list_item_title).text = reports[position].title
        holder.linearLayout.findViewById<TextView>(R.id.report_list_item_location).text = reports[position].details.type + ", " + DateFormat.getInstance().format(reports[position].createdAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.report_list_item, parent, false) as LinearLayout

        return ReportListViewHolder(linearLayout)
    }

}