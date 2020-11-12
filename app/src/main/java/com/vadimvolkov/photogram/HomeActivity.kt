package com.vadimvolkov.photogram

import android.os.Bundle
import android.util.Log

class HomeActivity : MainActivity(0) {

    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}