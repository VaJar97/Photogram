package com.vadimvolkov.photogram.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.vadimvolkov.photogram.models.User

interface UsersRepository {

    fun getUsers(): LiveData<List<User>>
    fun userUid(): String?
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun removeFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun removeFollower(fromUid: String, toUid: String): Task<Unit>

    fun getUser(): LiveData<User>
    abstract fun uploadUserPhoto(localImage: Uri): Task<Uri>
    abstract fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    abstract fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    abstract fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>

}