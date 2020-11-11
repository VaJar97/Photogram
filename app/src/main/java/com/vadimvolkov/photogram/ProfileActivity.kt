package com.vadimvolkov.photogram

import android.os.Bundle
import android.util.Log

class ProfileActivity : MainActivity(4) {

    private val TAG = "ProfileActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}