package com.vadimvolkov.photogram.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.*
import kotlinx.android.synthetic.main.activity_add_friend.*
import kotlinx.android.synthetic.main.add_friend_item.view.*

class AddFriendActivity : AppCompatActivity(), FriendsAdapter.Listener {
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mUsers: List<User>
    private lateinit var mUser: User
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        firebaseHelper = FirebaseHelper(this)
        val uid = firebaseHelper.currentUserUid()!!

        image_back.setOnClickListener {
            finish()
        }

        mAdapter = FriendsAdapter(this)
        recycler_add_friend.adapter = mAdapter
        recycler_add_friend.layoutManager = LinearLayoutManager(this)

        firebaseHelper.mDatabaseRef.child("users")
                .addValueEventListener(ValueEventListenerAdapter{
                    val allUsers = it.children.map { it.asUser()!! }
                    val (userList, otherUsersList) = allUsers.partition { it.uid == uid }
                    mUser = userList.first()
                    mUsers = otherUsersList

                    mAdapter.update(mUsers, mUser.follows)
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

        val followTask = firebaseHelper.mDatabaseRef.child("users")
                .child(mUser.uid!!).child("follows").child(uid)
        val setFollow = if (follow) followTask.setValue(follow) else followTask.removeValue()

        val followersTask = firebaseHelper.mDatabaseRef.child("users")
                .child(uid).child("followers").child(mUser.uid!!)
        val setFollower = if (follow) followersTask.setValue(follow) else followersTask.removeValue()

        val feedPostsTask = task<Void> {taskSource ->
            firebaseHelper.mDatabaseRef.child("feed-posts").child(uid)
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                        val postsMap = if (follow) {
                            it.children.map { it.key to it.value }.toMap()
                        } else {
                            it.children.map { it.key to null }.toMap()
                        }
                        firebaseHelper.mDatabaseRef.child("feed-posts")
                                .child(mUser.uid!!).updateChildren(postsMap)
                                .addOnCompleteListener { TaskSourceOnCompleteListener(taskSource) }
                    })
        }

        Tasks.whenAll(setFollow, setFollower, feedPostsTask)
        setFollow.continueWithTask { setFollower }.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }
}

class FriendsAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var mPositions = mapOf<String?, Int>()
    private var mFollows =  mapOf<String, Boolean>()
    private var mUsers =  listOf<User>()

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_friend_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val user = mUsers[position] // user = current view in recycler
            view.photo_add_friend.loadUserPhoto(user.photo)
            view.username_add_friend.text = user.username
            view.name_add_friend.text = user.name
            val follows = mFollows[user.uid] ?: false   // if null -> false

            view.follow_btn.setOnClickListener { listener.follow(user.uid!!) }
            view.unfollow_btn.setOnClickListener { listener.unfollow(user.uid!!) }

            if (follows) {
                // if we already follow to other user
                view.follow_btn.visibility = View.GONE
                view.unfollow_btn.visibility = View.VISIBLE
            } else {
                view.follow_btn.visibility = View.VISIBLE
                view.unfollow_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        mFollows = follows
        mPositions = users.withIndex().map { (idx, user) -> user.uid!! to idx }.toMap()
        mUsers = users
        notifyDataSetChanged()
    }

    fun followed(uid: String) {
        mFollows += uid to true
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows -= uid
        notifyItemChanged(mPositions[uid]!!)
    }
}

