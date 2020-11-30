package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.utils.CameraHelper
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : MainActivity(2) {

    private lateinit var mCameraHelper: CameraHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()

        image_back.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == mCameraHelper.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this).load(mCameraHelper.photoUri).centerCrop().into(image_share)
            // center crop - for square image
        }
    }
}