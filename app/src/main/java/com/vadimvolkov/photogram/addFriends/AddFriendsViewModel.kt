package com.vadimvolkov.photogram.addFriends

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.vadimvolkov.photogram.data.FeedPostsRepository
import com.vadimvolkov.photogram.data.UsersRepository
import com.vadimvolkov.photogram.data.common.map
import com.vadimvolkov.photogram.models.User

class AddFriendsViewModel(
    private val failureListener: OnFailureListener,
    private val usersRepo: UsersRepository,
    private val feedPostsRepo: FeedPostsRepository): ViewModel() {

    val userAndFriends: LiveData<Pair<User, List<User>>> =
        usersRepo.getUsers().map { allUsers ->
            val (userList, otherUsersList) = allUsers.partition {
                it.uid == usersRepo.userUid()
            }
            userList.first() to otherUsersList
        }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return( if (follow) {
            Tasks.whenAll(
                usersRepo.addFollow(currentUid, uid),
                usersRepo.addFollower(currentUid, uid),
                feedPostsRepo.copyFeedPosts(postsAuthorUid = uid, currentUid)
            )
        } else {
            Tasks.whenAll(
                    usersRepo.removeFollow(currentUid, uid),
                    usersRepo.removeFollower(currentUid, uid),
                    feedPostsRepo.removeFeedPosts(postsAuthorUid = uid, uid = currentUid)
            )
        }).addOnFailureListener(failureListener)
    }
}