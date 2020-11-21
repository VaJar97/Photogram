package com.vadimvolkov.photogram.activities

import android.util.Log
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

//fun Context.toast(text : String, duration: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, text, duration).show()
//}