package com.vadimvolkov.photogram.data.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <A, B> LiveData<A>.map(f: (A) -> B) =
            Transformations.map(this, f)
