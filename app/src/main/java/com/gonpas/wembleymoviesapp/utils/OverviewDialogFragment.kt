package com.gonpas.wembleymoviesapp.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class OverviewDialogFragment(val title: String, private val msg: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok)
            { dialog, _ ->
                dialog.dismiss()
            }
            builder.create()
        } ?: throw java.lang.IllegalStateException("¡¡¡ Activity null !!!")
    }
}