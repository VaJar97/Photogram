package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*
import com.vadimvolkov.photogram.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"

    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private lateinit var mUrl: String

    private lateinit var cameraHelper: CameraHelper
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate: ")

        image_close.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        cameraHelper = CameraHelper(this)
        firebaseHelper = FirebaseHelper(this)

        image_save.setOnClickListener { saveUpdateInfo() }
        change_photo_text.setOnClickListener { cameraHelper.takeCameraPicture() }

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mDatabaseRef.child("users")
                .child(user!!.uid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.getValue(User::class.java)!!
                    name_edit.setText(mUser.name)
                    username_edit.setText(mUser.username)
                    website_edit.setText(mUser.website)
                    bio_edit.setText(mUser.bio)
                    email_edit.setText(mUser.email)
                    phone_edit.setText(mUser.phone)
                    profile_image_edit.loadUserPhoto(mUser.photo)
                })
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == cameraHelper.REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK)) {
            // upload image to firebase storage
            firebaseHelper.uploadPhotoToStorage(mAuth = mAuth, uri = cameraHelper.photoUri) {
               firebaseHelper.getDownloadUrl(mAuth = mAuth) {
                   mUrl = it.result.toString()
                   firebaseHelper.uploadDatabasePhotoUrl(mAuth = mAuth, mUrl = mUrl) {
                       mUser = mUser.copy(photo = mUrl)
                       profile_image_edit.loadUserPhoto(mUser.photo)
                       Log.d(TAG, "onActivityResult: Photo saved successfully")
                   }
                }
            }
        }
    }

    private fun saveUpdateInfo() {

        mPendingUser = User(
                name = name_edit.text.toString(),
                username = username_edit.text.toString(),
                email = email_edit.text.toString(),
                phone = phone_edit.text.toStringOrNull(),
                bio = bio_edit.text.toStringOrNull(),
                website = website_edit.text.toStringOrNull()
        )
        val error = validate(mPendingUser)
        if (error == null) {
            if (mPendingUser.email == mUser.email) {
                updateUser(mPendingUser)
            } else {
                //show dialog
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        } else {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(user: User): String? =
            when {
                user.name.isEmpty() -> "Please enter name"
                user.username.isEmpty() -> "Please enter username"
                user.email.isEmpty() -> "Please enter email"
                else -> null
            }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio

        mDatabaseRef.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showToast("Successfully updated")
                        finish()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
    }

    override fun onPasswordConfirm(password: String) {
        // re-authenticate
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            firebaseHelper.reauthenticate(credential) {
                firebaseHelper.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            showToast("You should enter password")
        }
    }
}

