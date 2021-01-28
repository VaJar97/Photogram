package com.vadimvolkov.photogram.data.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.vadimvolkov.photogram.common.toUnit
import com.vadimvolkov.photogram.data.UsersRepository
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.common.task
import com.vadimvolkov.photogram.data.common.map
import com.vadimvolkov.photogram.data.firebase.common.*
import java.lang.IllegalStateException

class FirebaseUsersRepository: UsersRepository {

    override fun getUsers(): LiveData<List<User>> =
        mDatabaseRef.child("users").liveData().map {
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

    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid)
            .setValue(true)
            .toUnit()

    override fun removeFollower(fromUid: String, toUid: String): Task<Unit> =
        getFollowersRef(fromUid, toUid)
            .removeValue()
            .toUnit()

    override fun userUid() =
        FirebaseAuth.getInstance().currentUser?.uid

    private fun getFollowsRef(fromUid: String, toUid: String) =
        mDatabaseRef.child("users")
            .child(fromUid).child("follows").child(toUid)

    private fun getFollowersRef(fromUid: String, toUid: String) =
        mDatabaseRef.child("users")
            .child(toUid).child("followers").child(fromUid)

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
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio

        return mDatabaseRef.child("users").child(currentUserUid()!!).updateChildren(updatesMap).toUnit()
    }
}
