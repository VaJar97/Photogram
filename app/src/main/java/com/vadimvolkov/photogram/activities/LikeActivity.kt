package com.vadimvolkov.photogram.activities

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R

class LikeActivity : MainActivity(3) {

    private val TAG = "LikeActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}