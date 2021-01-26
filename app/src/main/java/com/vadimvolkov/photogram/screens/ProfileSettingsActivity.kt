package com.vadimvolkov.photogram.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.data.firebase.common.FirebaseHelper
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity: AppCompatActivity() {
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        firebaseHelper = FirebaseHelper(this)

        profile_sign_out.setOnClickListener { firebaseHelper.mAuth.signOut() }
        image_back.setOnClickListener { finish() }
    }
}
