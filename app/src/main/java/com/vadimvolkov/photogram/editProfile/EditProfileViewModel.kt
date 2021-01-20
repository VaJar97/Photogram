package com.vadimvolkov.photogram.editProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.currentUserUid
import com.vadimvolkov.photogram.utils.mDatabaseRef
import com.vadimvolkov.photogram.utils.mStorageRef

class EditProfileViewModel(private val repository: EditProfileRepository): ViewModel() {

    val user: LiveData<User> = repository.getUser()

    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
        repository.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
            repository.updateUserPhoto(downloadUrl!!)
        }

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        repository.updateEmail(
                currentEmail = currentEmail,
                newEmail = newEmail,
                password = password
        )

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        repository.updateUserProfile(currentUser = currentUser, newUser = newUser)
}