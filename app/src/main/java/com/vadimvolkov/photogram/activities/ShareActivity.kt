package com.vadimvolkov.photogram.activities

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R

class ShareActivity : MainActivity(2) {

    private val TAG = "ShareActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}