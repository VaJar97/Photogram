package com.vadimvolkov.photogram

import android.os.Bundle
import android.util.Log

class SearchActivity : MainActivity(1) {

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}