package edu.festival.deichkind.listeners

import android.view.View
import android.widget.AdapterView

abstract class OnItemSelectedListener : AdapterView.OnItemSelectedListener {

    abstract override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}