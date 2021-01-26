package com.vadimvolkov.photogram.screens

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.data.firebase.common.*
import com.vadimvolkov.photogram.models.FeedPost
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.screens.common.MainActivity
import com.vadimvolkov.photogram.common.ValueEventListenerAdapter
import com.vadimvolkov.photogram.screens.common.CameraHelper
import com.vadimvolkov.photogram.screens.common.showToast
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : MainActivity() {

    private lateinit var mCameraHelper: CameraHelper
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()

        image_back.setOnClickListener { finish() }
        toolbar_text_share.setOnClickListener { sharePhoto() }

        firebaseHelper = FirebaseHelper(this)

        firebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.asUser()!!
        })
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
        mCameraHelper.photoUri?.let { photoUri ->
            val uid = currentUserUid()!!
            mStorageRef
                    .child("users")
                    .child(uid)
                    .child("images")
                    .child(photoUri.lastPathSegment!!)
                    .putFile(photoUri)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                it.result!!.storage.downloadUrl.addOnCompleteListener {
                                    val url = it.result!!.toString()
                                    mDatabaseRef
                                        .child("images")
                                        .child(uid)
                                        .push()
                                        .setValue(url)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                mDatabaseRef.child("feed-posts")
                                                        .child(uid)
                                                        .push()
                                                        .setValue(makeFeedPost(uid, url))
                                                        .addOnCompleteListener {
                                                            if (it.isSuccessful) {
                                                                startActivity(Intent(this, ProfileActivity::class.java))
                                                                finish()
                                                            } else {
                                                                this.showToast(it.exception!!.message!!)
                                                            }
                                                        }
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

    private fun makeFeedPost(uid: String, url: String) = FeedPost (
            uid = uid,
            username = mUser.username,
            postImage = url,
            caption = capture_share_text.text.toString(),
            userPhoto = mUser.photo
    )
}

