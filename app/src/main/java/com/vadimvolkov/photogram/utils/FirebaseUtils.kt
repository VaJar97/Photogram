package com.vadimvolkov.photogram.utils

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

    val mDatabaseRef = FirebaseDatabase.getInstance().reference
    val mStorageRef = FirebaseStorage.getInstance().reference
    val mAuth = FirebaseAuth.getInstance()
    fun currentUserUid(): String? = mAuth.currentUser?.uid

