package com.vadimvolkov.photogram.editProfile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.vadimvolkov.photogram.data.UsersRepository
import com.vadimvolkov.photogram.models.User

class EditProfileViewModel(private val failureListener: OnFailureListener,
                           private val usersRepo: UsersRepository): ViewModel() {

    val user: LiveData<User> = usersRepo.getUser()

    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
        usersRepo.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
            usersRepo.updateUserPhoto(downloadUrl!!)
        }.addOnFailureListener(failureListener)

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
        usersRepo.updateEmail(
                currentEmail = currentEmail,
                newEmail = newEmail,
                password = password
        ).addOnFailureListener(failureListener)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
        usersRepo.updateUserProfile(currentUser = currentUser, newUser = newUser)
            .addOnFailureListener(failureListener)
}