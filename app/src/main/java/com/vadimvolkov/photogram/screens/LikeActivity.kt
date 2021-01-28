package com.vadimvolkov.photogram.screens

import android.os.Bundle
import android.util.Log
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.screens.common.MainActivity
import com.vadimvolkov.photogram.screens.common.setupBottomNavigation

class LikeActivity : MainActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like)
        setupBottomNavigation(3)
    }
}