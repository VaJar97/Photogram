package com.vadimvolkov.photogram

import android.os.Bundle
import android.util.Log

class ShareActivity : MainActivity(2) {

    private val TAG = "ShareActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}