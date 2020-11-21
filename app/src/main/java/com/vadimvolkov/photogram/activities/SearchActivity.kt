package com.vadimvolkov.photogram.activities

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R

class SearchActivity : MainActivity(1) {

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")
    }
}