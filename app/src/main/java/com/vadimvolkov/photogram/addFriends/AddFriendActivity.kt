package com.vadimvolkov.photogram.addFriends

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.activities.ViewModelFactory
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.showToast
import kotlinx.android.synthetic.main.activity_add_friend.*

class AddFriendActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mUsers: List<User>
    private lateinit var mUser: User
    private lateinit var mViewModel: AddFriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        mViewModel = ViewModelProvider(this, ViewModelFactory())
            .get(AddFriendsViewModel::class.java)

        image_back.setOnClickListener {
            finish()
        }

        mAdapter = FriendsAdapter(this)
        recycler_add_friend.adapter = mAdapter
        recycler_add_friend.layoutManager = LinearLayoutManager(this)

        mViewModel.userAndFriends.observe(this, Observer {
            it?.let { (user, otherUsers) ->
                mUser = user
                mUsers = otherUsers
                mAdapter.update(mUsers, mUser.follows) }
        })
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)

        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        mViewModel.setFollow(mUser.uid!!, uid, follow)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { showToast(it.message!!) }
    }

}