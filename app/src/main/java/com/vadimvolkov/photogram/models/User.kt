package com.vadimvolkov.photogram.models

import com.google.firebase.database.Exclude

data class User(
        val name: String = "",
        val username: String = "",
        val email: String = "",
        @Exclude val uid: String = "",
        val follows: Map<String, Boolean> = emptyMap(),
        val followers: Map<String, Boolean> = emptyMap(),
        val website: String? = null,
        val bio: String? = null,
        val phone: String? = null,
        val photo: String? = null
)
