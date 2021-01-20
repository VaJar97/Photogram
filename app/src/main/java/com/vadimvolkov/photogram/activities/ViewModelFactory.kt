package com.vadimvolkov.photogram.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vadimvolkov.photogram.addFriends.AddFriendsViewModel
import com.vadimvolkov.photogram.addFriends.FirebaseAddFriendsRepository
import com.vadimvolkov.photogram.editProfile.EditProfileRepository
import com.vadimvolkov.photogram.editProfile.EditProfileViewModel
import com.vadimvolkov.photogram.editProfile.FirebaseEditProfileRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(FirebaseAddFriendsRepository()) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(FirebaseEditProfileRepository()) as T
        } else {
                error("Unknown model class $modelClass")
        }
    }
}