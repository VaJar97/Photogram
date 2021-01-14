package com.vadimvolkov.photogram.addFriends

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*

interface AddFriendsRepository {
    fun getUsers(): LiveData<List<User>>
    fun userUid(): String?
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun removeFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun removeFollower(fromUid: String, toUid: String): Task<Unit>
    fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
    fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit>
}

class FirebaseAddFriendsRepository: AddFriendsRepository {

    private val reference = FirebaseDatabase.getInstance().reference

    override fun getUsers(): LiveData<List<User>> =
        FirebaseLiveData(reference.child("users")).map {
           it.children.map { it.asUser()!! }
        }

    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollowsRef(fromUid, toUid)
            .setValue(true)
            .toUnit()

    override fun removeFollow(fromUid: String, toUid: String): Task<Unit> =
        getFollowsRef(fromUid, toUid)
            .removeValue()
            .toUnit()

    private fun getFollowsRef(fromUid: String, toUid: String) =
        reference.child("users")
            .child(fromUid).child("follows").child(toUid)

    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid)
            .setValue(true)
            .toUnit()

    override fun removeFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid)
            .removeValue()
            .toUnit()

    private fun getFollowersRef(fromUid: String, toUid: String) =
        reference.child("users")
            .child(toUid).child("followers").child(fromUid)

    override fun userUid() =
        FirebaseAuth.getInstance().currentUser?.uid

    override fun copyFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            reference.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)    // другой пользователь имеет и чужие посты у себя в базе, поэтому фильтруем только созданные им
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to it.value }.toMap()
                    reference.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource) )
                })
        }

    override fun removeFeedPosts(postsAuthorUid: String, uid: String): Task<Unit> =
        task { taskSource ->
            reference.child("feed-posts").child(postsAuthorUid)
                .orderByChild("uid")
                .equalTo(postsAuthorUid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val postsMap = it.children.map { it.key to null }.toMap()
                    reference.child("feed-posts")
                        .child(uid).updateChildren(postsMap)
                        .toUnit()
                        .addOnCompleteListener(TaskSourceOnCompleteListener(taskSource) )
                })
        }
}

fun Task<Void>.toUnit(): Task<Unit> =
    onSuccessTask { Tasks.forResult(Unit) }