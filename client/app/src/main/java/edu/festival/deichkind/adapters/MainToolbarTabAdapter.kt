package edu.festival.deichkind.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import edu.festival.deichkind.R
import edu.festival.deichkind.fragments.DykeListFragment
import edu.festival.deichkind.fragments.ReportListFragment

class MainToolbarTabAdapter (fm: FragmentManager, c: Context) : FragmentPagerAdapter(fm) {

    private val context: Context = c

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(itemId: Int): Fragment? {
        return when (itemId) {
            0 -> ReportListFragment()
            1 -> DykeListFragment()
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.main_tab_reports_name)
            1 -> context.getString(R.string.main_tab_dykes_name)
            else -> ""
        }
    }

}