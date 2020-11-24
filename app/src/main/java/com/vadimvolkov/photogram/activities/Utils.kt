package com.vadimvolkov.photogram.activities

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ValueEventListenerAdapter(val handler : (DataSnapshot) -> Unit) : ValueEventListener {

    private val TAG = "ValueEventListenerAdapt"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "onCancelled: Error with databaseRefValueListener", error.toException())
    }
}

fun coordinateBtnAndInputs(btn : Button, vararg inputs : EditText) {
    val watcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all {
                it.text.isNotEmpty()
            }
        }
    }
    btn.isEnabled = inputs.all {
        it.text.isNotEmpty()
    }   // for default disabled mod
    inputs.forEach { it.addTextChangedListener(watcher) }
}

//fun Context.toast(text : String, duration: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, text, duration).show()
//}