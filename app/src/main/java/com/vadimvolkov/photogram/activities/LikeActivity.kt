package com.vadimvolkov.photogram.activities

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.views.setupBottomNavigation

class LikeActivity : MainActivity() {

    private val TAG = "LikeActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate: ")
    }
}