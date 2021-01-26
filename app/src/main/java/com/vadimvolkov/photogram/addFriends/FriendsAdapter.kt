package com.vadimvolkov.photogram.addFriends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.screens.common.loadUserPhoto
import com.vadimvolkov.photogram.utils.*
import kotlinx.android.synthetic.main.add_friend_item.view.*

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

