package com.vadimvolkov.photogram.screens

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.screens.common.MainActivity
import com.vadimvolkov.photogram.screens.common.setupBottomNavigation

class SearchActivity : MainActivity() {

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupBottomNavigation(1)
        Log.d(TAG, "onCreate: ")
    }
}