package com.vadimvolkov.photogram.utils

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

    private val mDatabaseRef = FirebaseDatabase.getInstance().reference
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private val mAuth = FirebaseAuth.getInstance()

    fun currentUserReference() = mDatabaseRef.child("users").child(mAuth.currentUser!!.uid)

    fun uploadPhotoToStorage(mAuth: FirebaseAuth, uri: Uri?, onSuccess: () -> Unit) {
        mStorageRef.child("users/${mAuth.currentUser!!.uid}/photo").putFile(uri!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }

    fun getDownloadUrl(mAuth: FirebaseAuth, onSuccess: (it: Task<Uri>) -> Unit) {
        mStorageRef.child("users/${mAuth.currentUser!!.uid}/photo").downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it)
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }

    fun uploadDatabasePhotoUrl(mAuth: FirebaseAuth, mUrl: String, onSuccess: () -> Unit) {
        mDatabaseRef.child("users/${mAuth.currentUser!!.uid}/photo")
            .setValue(mUrl)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        mAuth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        mAuth.currentUser!!.updateEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    activity.showToast(it.exception!!.message!!)
                }
            }
    }
}