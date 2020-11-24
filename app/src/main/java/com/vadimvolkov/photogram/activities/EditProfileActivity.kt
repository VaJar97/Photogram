package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.views.PasswordDialog
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference

    private lateinit var photoUri: Uri
    private lateinit var mURL: String
    private val REQUEST_IMAGE_CAPTURE = 1
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate: ")

        image_close.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        image_save.setOnClickListener { saveUpdateInfo() }
        change_photo_text.setOnClickListener { takeCameraPicture() }

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mDatabaseRef.child("users")
                .child(user!!.uid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    mUser = it.getValue(User::class.java)!!
                    name_edit.setText(mUser.name, TextView.BufferType.EDITABLE)
                    username_edit.setText(mUser.username, TextView.BufferType.EDITABLE)
                    website_edit.setText(mUser.website, TextView.BufferType.EDITABLE)
                    bio_edit.setText(mUser.bio, TextView.BufferType.EDITABLE)
                    email_edit.setText(mUser.email, TextView.BufferType.EDITABLE)
                    phone_edit.setText(mUser.phone.toString(), TextView.BufferType.EDITABLE)
                })
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    private fun takeCameraPicture() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    val photoFile = createPhotoFile()
                    photoUri = FileProvider.getUriForFile(
                            this,
                            "com.vadimvolkov.photogram.fileprovider",
                            photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK)) {
            // upload image to firebase storage
            mStorageRef.child("users/${mAuth.currentUser!!.uid}/photo").putFile(photoUri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        mStorageRef.child("users/${mAuth.currentUser!!.uid}/photo").downloadUrl
                            .addOnCompleteListener{
                                if (it.isSuccessful) {
                                    mURL = it.result.toString()

                                    mDatabaseRef.child("users/${mAuth.currentUser!!.uid}/photo")
                                        .setValue(mURL)
                                        .addOnCompleteListener{
                                            if (it.isSuccessful) {
                                                Log.d(TAG, "onActivityResult: Photo saved successfully")
                                            } else {
                                                Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                }
                            }
                    } else {
                        Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun createPhotoFile(): File {
        // Create an image file name
        val timeStamp: String = simpleDateFormat.format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }

    private fun saveUpdateInfo() {

        mPendingUser = User(
                name = name_edit.text.toString(),
                username = username_edit.text.toString(),
                email = email_edit.text.toString(),
                phone = phone_edit.text.toString(),
                bio = bio_edit.text.toString(),
                website = website_edit.text.toString()
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
        val updatesMap = mutableMapOf<String, Any>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio

        mDatabaseRef.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Successfully updated", Toast.LENGTH_SHORT)
                                .show()
                        finish()
                    } else {
                        Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
    }

    override fun onPasswordConfirm(password: String) {
        // re-authenticate
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential) {
                mAuth.currentUser!!.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        } else {
            Toast.makeText(this, "You should enter password", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
        reauthenticate(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess
                    } else {
                        Toast.makeText(this@EditProfileActivity, it.exception!!.message!!, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
    }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
        updateEmail(mPendingUser.email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateUser(mPendingUser)
                    } else {
                        Toast.makeText(this@EditProfileActivity, it.exception!!.message!!, Toast.LENGTH_SHORT)
                                .show()
                    }
                }
    }
}