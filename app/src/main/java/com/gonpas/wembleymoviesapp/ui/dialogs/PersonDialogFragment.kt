package com.gonpas.wembleymoviesapp.ui.dialogs

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.databinding.DialogPersonBinding
import com.gonpas.wembleymoviesapp.domain.DomainPerson
import kotlinx.parcelize.Parcelize

private const val TAG = "xxPdf"

class PersonDialogFragment()
    : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val person: DomainPerson = arguments?.getParcelable("person")!!
        val dismissDialogListener: DismissDialogListener = arguments?.getParcelable("listener")!!

        val binding = DataBindingUtil.inflate<DialogPersonBinding>(inflater, R.layout.dialog_person,container, false)

        binding.person = person
        binding.okButton.setOnClickListener {
            dialog!!.dismiss()
            dismissDialogListener.onDismiss()
        }


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.85).toInt()
        dialog!!.window!!.setLayout(width,height)
    }

    companion object{
        fun newInstance(person: DomainPerson, dismissDialogListener: DismissDialogListener): PersonDialogFragment {
            val args = Bundle()
            args.putParcelable("person", person)
            args.putParcelable("listener", dismissDialogListener)
            val fragment = PersonDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Parcelize
    class DismissDialogListener(val dismissListener: () -> Unit): Parcelable{
        fun onDismiss() = dismissListener()
    }
}