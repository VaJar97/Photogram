package com.vadimvolkov.photogram.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.vadimvolkov.photogram.models.User

class FirebaseLiveData(private val reference: DatabaseReference) : LiveData<DataSnapshot>() {
    private val listener = ValueEventListenerAdapter {
        value = it
    }
    override fun onActive() {
        super.onActive()
        reference.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        reference.removeEventListener(listener)
    }
}