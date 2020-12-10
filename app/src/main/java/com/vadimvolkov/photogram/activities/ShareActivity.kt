package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.StorageReference
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_share.*
import org.w3c.dom.Comment
import java.util.*

class ShareActivity : MainActivity(2) {

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
        if (mCameraHelper.photoUri != null) {
            val uid = firebaseHelper.mAuth.currentUser!!.uid
            val photoUri = mCameraHelper.photoUri!!
            firebaseHelper.mStorageRef
                    .child("users")
                    .child(uid)
                    .child("images")
                    .child(photoUri.lastPathSegment!!)
                    .putFile(photoUri)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                it.result!!.storage.downloadUrl.addOnCompleteListener {
                                    val url = it.result!!.toString()
                                    firebaseHelper.mDatabaseRef
                                        .child("images")
                                        .child(uid)
                                        .push()
                                        .setValue(url)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                firebaseHelper.mDatabaseRef.child("feed-posts")
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

    private fun makeFeedPost(uid: String, url: String) = FeedPost(
            uid = uid,
            username = mUser.username,
            postImage = url,
            caption = capture_share_text.text.toString(),
            userPhoto = mUser.photo
    )
}

data class FeedPost(val uid: String = "", val username: String = "", val userPhoto: String? = null,
                    val postImage: String = "", val likesCount: Int = 0, val commentsCount: Int = 0,
                    val caption: String = "", val comments: List<Comment> = emptyList(),
                    val timeStamp: Any = ServerValue.TIMESTAMP) {

    fun timeStampDate(): Date = Date(timeStamp as Long)
}

data class Commet(val uid: String, val username: String,val comment: String)