package edu.festival.deichkind.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.festival.deichkind.R
import java.lang.IllegalStateException

class NoSessionDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setMessage(getString(R.string.create_report_no_session_notice))
                .setPositiveButton("Ok") { dialog, which ->

                }
            builder.create()
        } ?: throw IllegalStateException()
    }
}
