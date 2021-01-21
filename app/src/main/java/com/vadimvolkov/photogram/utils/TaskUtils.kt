package com.vadimvolkov.photogram.addFriends

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

fun Task<Void>.toUnit(): Task<Unit> =
    onSuccessTask { Tasks.forResult(Unit) }