package com.vadimvolkov.photogram.data

import com.google.android.gms.tasks.Task

interface FeedPostsRepository {
  fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
}