package com.vadimvolkov.photogram.editProfile

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.vadimvolkov.photogram.addFriends.toUnit
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*
import java.lang.IllegalStateException

interface EditProfileRepository {
    fun getUser(): LiveData<User>
    abstract fun uploadUserPhoto(localImage: Uri): Task<Uri>
    abstract fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    abstract fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    abstract fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>

}

class FirebaseEditProfileRepository: EditProfileRepository {
    override fun getUser(): LiveData<User> =
            mDatabaseRef.child("users").child(currentUserUid()!!).liveData().map {
                it.asUser()
            }

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
            task {taskSource ->
                mStorageRef.child("users/${currentUserUid()!!}/photo").putFile(localImage)
                        .addOnSuccessListener {
                            it.storage.downloadUrl.addOnCompleteListener {
                                taskSource.setResult(it.result)
                            }
                        }
            }

    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
            mDatabaseRef.child("users/${currentUserUid()!!}/photo")
                    .setValue(downloadUrl.toString())
                    .toUnit()

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = mAuth.currentUser
        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)

            currentUser.reauthenticate(credential).onSuccessTask {
                currentUser.updateEmail(newEmail)
            }.toUnit()
        } else {
            Tasks.forException(IllegalStateException("User is not authenticated"))
        }
    }

    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["newUsername"] = newUser.username
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio

        return mDatabaseRef.child("users").child(currentUserUid()!!).updateChildren(updatesMap).toUnit()
    }
}

