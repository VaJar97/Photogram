package com.vadimvolkov.photogram.common

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ValueEventListenerAdapter(val handler : (DataSnapshot) -> Unit) : ValueEventListener {

    private val TAG = "utils.ValueEventListenerAdapt"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "onCancelled: Error with databaseRefValueListener", error.toException())
    }
}