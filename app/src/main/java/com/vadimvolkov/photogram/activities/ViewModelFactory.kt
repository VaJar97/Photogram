package com.vadimvolkov.photogram.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.vadimvolkov.photogram.addFriends.AddFriendsViewModel
import com.vadimvolkov.photogram.data.firebase.FirebaseFeedPostsRepository
import com.vadimvolkov.photogram.editProfile.EditProfileViewModel
import com.vadimvolkov.photogram.data.firebase.FirebaseUsersRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val failureListener: OnFailureListener): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(failureListener, FirebaseUsersRepository(), FirebaseFeedPostsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(failureListener, FirebaseUsersRepository()) as T
        } else {
                error("Unknown model class $modelClass")
        }
    }
}