package com.vadimvolkov.photogram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private val TAG = "EditProfileActivity"

    private lateinit var mAuth : FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate: ")

        image_close.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            finish()
        })

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val dataRef = FirebaseDatabase.getInstance().getReference()
            .child("users")
            .child(user!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val userData = it.getValue(User::class.java)
                    name_edit.setText(userData!!.name, TextView.BufferType.EDITABLE)
                    username_edit.setText(userData.username, TextView.BufferType.EDITABLE)
                    website_edit.setText(userData.website, TextView.BufferType.EDITABLE)
                    bio_edit.setText(userData.bio, TextView.BufferType.EDITABLE)
                    email_edit.setText(userData.email, TextView.BufferType.EDITABLE)
                    phone_edit.setText(userData.phone.toString(), TextView.BufferType.EDITABLE)
            })
    }
}