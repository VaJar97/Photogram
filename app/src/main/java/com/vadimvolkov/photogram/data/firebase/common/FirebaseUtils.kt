package com.vadimvolkov.photogram.data.firebase.common

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vadimvolkov.photogram.models.FeedPost
import com.vadimvolkov.photogram.models.User

fun DataSnapshot.asUser(): User? =
        getValue(User::class.java)?.copy(uid = key)

fun DataSnapshot.asFeedPost(): FeedPost? =
    getValue(FeedPost::class.java)?.copy(id = key)

val mDatabaseRef = FirebaseDatabase.getInstance().reference
val mStorageRef = FirebaseStorage.getInstance().reference
val mAuth = FirebaseAuth.getInstance()
fun currentUserUid(): String? = mAuth.currentUser?.uid
fun DatabaseReference.liveData(): LiveData<DataSnapshot> =
    FirebaseLiveData(this)