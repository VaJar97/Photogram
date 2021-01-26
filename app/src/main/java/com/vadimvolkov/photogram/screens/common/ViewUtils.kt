package com.vadimvolkov.photogram.screens.common

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.vadimvolkov.photogram.R

fun coordinateBtnAndInputs(btn : Button, vararg inputs : EditText) {
    val watcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled = inputs.all {
                it.text.isNotEmpty()
            }
        }
    }
    btn.isEnabled = inputs.all {
        it.text.isNotEmpty()
    }   // for default disabled mod
    inputs.forEach { it.addTextChangedListener(watcher) }
}

fun ImageView.loadUserPhoto(url: String?) {
    if (!(context as Activity).isDestroyed) {
        Glide.with(this).load(url).fallback(R.drawable.person).into(this)
    }
}

fun Editable.toStringOrNull(): String? {
    val str = toString()
    return if (str.isEmpty()) null else str
}

fun Activity.showToast(text : String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun ImageView.loadImage(image: String?) {
    if (!(context as Activity).isDestroyed) {
        Glide.with(this).load(image).centerCrop().into(this)
    }
}

