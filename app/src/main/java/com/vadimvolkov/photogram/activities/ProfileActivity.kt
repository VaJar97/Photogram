package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.vadimvolkov.photogram.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : MainActivity(4) {

    private val TAG = "ProfileActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")

        edit_button.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        })
    }
}