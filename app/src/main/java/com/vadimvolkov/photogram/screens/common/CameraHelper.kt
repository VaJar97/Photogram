package com.vadimvolkov.photogram.screens.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity) {

    var photoUri: Uri? = null
    val REQUEST_IMAGE_CAPTURE = 1
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    fun takeCameraPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            val photoFile = createPhotoFile()
            photoUri = FileProvider.getUriForFile(
                activity,
                "com.vadimvolkov.photogram.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun createPhotoFile(): File {
        // Create an image file name
        val timeStamp: String = simpleDateFormat.format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )
    }
}