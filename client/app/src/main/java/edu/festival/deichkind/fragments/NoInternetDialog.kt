package edu.festival.deichkind.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

import edu.festival.deichkind.R
import java.lang.IllegalStateException

class NoInternetDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(getString(R.string.main_no_internet_notice))
                .setPositiveButton("Ok") { dialog, which ->

                }
            builder.create()
        } ?: throw IllegalStateException()
    }
}
