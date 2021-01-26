package com.vadimvolkov.photogram.data.firebase

import com.google.android.gms.tasks.Task
import com.vadimvolkov.photogram.common.TaskSourceOnCompleteListener
import com.vadimvolkov.photogram.common.toUnit
import com.vadimvolkov.photogram.data.FeedPostsRepository
import com.vadimvolkov.photogram.common.ValueEventListenerAdapter
import com.vadimvolkov.photogram.common.task
import com.vadimvolkov.photogram.data.firebase.common.mDatabaseRef

class FirebaseFeedPostsRepository: FeedPostsRepository {

    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            mDatabaseRef.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)    // другой пользователь имеет и чужие посты у себя в базе, поэтому фильтруем только созданные им
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    mDatabaseRef.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }

    override fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            mDatabaseRef.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    mDatabaseRef.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource))
                })
        }
}