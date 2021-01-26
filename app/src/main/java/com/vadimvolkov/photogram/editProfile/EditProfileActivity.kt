package com.vadimvolkov.photogram.editProfile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.screens.ProfileActivity
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.screens.common.*
import com.vadimvolkov.photogram.screens.common.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : MainActivity(), PasswordDialog.Listener {

    private lateinit var mViewModel: EditProfileViewModel

    private lateinit var mUser: User
    private lateinit var mPendingUser: User

    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        image_close.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        mViewModel = initViewModel()
        cameraHelper = CameraHelper(this)

        image_save.setOnClickListener { saveUpdateInfo() }
        change_photo_text.setOnClickListener { cameraHelper.takeCameraPicture() }

        mViewModel.user.observe(this, Observer {
            it?.let {
                mUser = it
                name_edit.setText(mUser.name)
                username_edit.setText(mUser.username)
                website_edit.setText(mUser.website)
                bio_edit.setText(mUser.bio)
                email_edit.setText(mUser.email)
                phone_edit.setText(mUser.phone)
                profile_image_edit.loadUserPhoto(mUser.photo)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == cameraHelper.REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK)) {
            // upload image to firebase storage
            mViewModel.uploadAndSetUserPhoto(cameraHelper.photoUri!!)
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
            showToast(error, Toast.LENGTH_SHORT)
        }
    }

    private fun validate(user: User): String? =
            when {
                user.name.isEmpty() -> getString(R.string.please_enter_name)
                user.username.isEmpty() -> getString(R.string.please_enter_username)
                user.email.isEmpty() -> getString(R.string.please_enter_email)
                else -> null
            }

    private fun updateUser(user: User) {
        mViewModel.updateUserProfile(currentUser = mUser, newUser = user)
                .addOnSuccessListener {
                    showToast(getString(R.string.successfuly_udated))
                    finish()
                }
    }

    override fun onPasswordConfirm(password: String) {
        // re-authenticate
        if (password.isNotEmpty()) {
            mViewModel.updateEmail(
                    currentEmail = mUser.email,
                    newEmail = mPendingUser.email,
                    password = password)
                    .addOnSuccessListener { updateUser(mPendingUser) }
                    .addOnFailureListener { showToast(it.message!!, Toast.LENGTH_SHORT) }
        } else {
            showToast(getString(R.string.you_should_enter_your_password))
        }
    }
}

