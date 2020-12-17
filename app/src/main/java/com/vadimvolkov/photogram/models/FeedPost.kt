package com.vadimvolkov.photogram.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import org.w3c.dom.Comment
import java.util.*

data class FeedPost(val uid: String = "", val username: String = "", val userPhoto: String? = null,
                    val postImage: String = "", @Exclude val commentsCount: Int = 0,
                    val caption: String = "", val comments: List<Comment> = emptyList(),
                    val timeStamp: Any = ServerValue.TIMESTAMP, @Exclude val id: String? = "") {

    fun timeStampDate(): Date = Date(timeStamp as Long)
}