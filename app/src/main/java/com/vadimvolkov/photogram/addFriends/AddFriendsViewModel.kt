package com.vadimvolkov.photogram.addFriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*

class AddFriendsViewModel(private val repository: AddFriendsRepository): ViewModel() {

    val userAndFriends: LiveData<Pair<User, List<User>>> =
        repository.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == repository.userUid()
            }
            userList.first() to otherUsersList
        }
    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return if (follow) {
            Tasks.whenAll(
                repository.addFollow(currentUid, uid),
                repository.addFollower(currentUid, uid),
                repository.copyFeedPosts(postsAuthorUid = uid, currentUid)
            )
        } else {
            Tasks.whenAll(
                    repository.removeFollow(currentUid, uid),
                    repository.removeFollower(currentUid, uid),
                    repository.removeFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        }
    }
}