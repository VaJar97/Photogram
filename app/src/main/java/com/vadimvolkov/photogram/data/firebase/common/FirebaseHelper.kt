package com.vadimvolkov.photogram.data.firebase.common

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FirebaseHelper (var activity: Activity) {

    val mDatabaseRef = FirebaseDatabase.getInstance().reference
    val mStorageRef = FirebaseStorage.getInstance().reference
    val mAuth = FirebaseAuth.getInstance()

    fun currentUserUid(): String? = mAuth.currentUser?.uid
    fun currentUserReference(): DatabaseReference = mDatabaseRef.child("users").child(currentUserUid()!!)

}