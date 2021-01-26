package com.vadimvolkov.photogram.screens.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.vadimvolkov.photogram.R
import kotlinx.android.synthetic.main.dialog_password.view.*

class PasswordDialog : DialogFragment() {

    private lateinit var mContext : Listener

    interface Listener {
        fun onPasswordConfirm(password : String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(context!!)
                .setView(view)
                .setPositiveButton(android.R.string.ok, {_, _ ->
                    mContext.onPasswordConfirm(view.dialog_password_input.text.toString())
                })
                .setNegativeButton(android.R.string.cancel, {_, _ ->
                    // do nothing
                })
                .setTitle(R.string.please_enter_password)
                .create()
    }
}