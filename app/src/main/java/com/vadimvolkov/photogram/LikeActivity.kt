package com.vadimvolkov.photogram

import android.os.Bundle
import android.util.Log

class LikeActivity : MainActivity(3) {

    private val TAG = "LikeActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}