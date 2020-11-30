package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.utils.CameraHelper
import com.vadimvolkov.photogram.utils.FirebaseHelper
import com.vadimvolkov.photogram.utils.showToast
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : MainActivity(2) {

    private lateinit var mCameraHelper: CameraHelper
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()

        image_back.setOnClickListener { finish() }
        toolbar_text_share.setOnClickListener { sharePhoto() }

        firebaseHelper = FirebaseHelper(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == mCameraHelper.REQUEST_IMAGE_CAPTURE) {
            if ( resultCode == RESULT_OK) {
                Glide.with(this).load(mCameraHelper.photoUri).centerCrop().into(image_share)    // center crop - for square image
            } else {
                finish()
            }
        }
    }

    private fun sharePhoto() {
        if (mCameraHelper.photoUri != null) {
            firebaseHelper.mStorageRef
                    .child("users")
                    .child(firebaseHelper.mAuth.currentUser!!.uid)
                    .child("photo")
                    .child(mCameraHelper.photoUri!!.lastPathSegment!!)
                    .putFile(mCameraHelper.photoUri!!)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                it.result!!.storage.downloadUrl.addOnCompleteListener {
                                    val url = it.result!!.toString()
                                    firebaseHelper.mDatabaseRef
                                        .child("images")
                                        .child(firebaseHelper.mAuth.currentUser!!.uid)
                                        .push()
                                        .setValue(url)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                startActivity(Intent(this,ProfileActivity::class.java))
                                                finish()
                                            } else {
                                                this.showToast(it.exception!!.message!!)
                                            }
                                        }
                                }

                            } else {
                                this.showToast(it.exception!!.message!!)
                            }
                    }
        }
    }
}