package edu.festival.deichkind.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import edu.festival.deichkind.R
import edu.festival.deichkind.models.Dyke

class DykeListAdapter(val dykes: Array<Dyke>) : RecyclerView.Adapter<DykeListAdapter.DykeListViewHolder>() {

    class DykeListViewHolder(val linearLayout: LinearLayout) : RecyclerView.ViewHolder(linearLayout)

    override fun getItemCount() = dykes.size

    override fun onBindViewHolder(holder: DykeListViewHolder, position: Int) {
        holder.linearLayout.findViewById<TextView>(R.id.dyke_list_item_name).text = dykes[position].name
        holder.linearLayout.findViewById<TextView>(R.id.dyke_list_item_location).text = dykes[position].city + ", " + dykes[position].state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DykeListViewHolder {
        val linearLayout = LayoutInflater.from(parent.context).inflate(R.layout.dyke_list_item, parent, false) as LinearLayout

        return DykeListViewHolder(linearLayout)
    }
}