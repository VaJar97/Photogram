package com.vadimvolkov.photogram.screens

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.data.firebase.common.FirebaseHelper
import com.vadimvolkov.photogram.data.firebase.common.asFeedPost
import com.vadimvolkov.photogram.data.firebase.common.mAuth
import com.vadimvolkov.photogram.data.firebase.common.mDatabaseRef
import com.vadimvolkov.photogram.models.FeedPost
import com.vadimvolkov.photogram.screens.common.MainActivity
import com.vadimvolkov.photogram.common.ValueEventListenerAdapter
import com.vadimvolkov.photogram.screens.common.loadImage
import com.vadimvolkov.photogram.screens.common.loadUserPhoto
import com.vadimvolkov.photogram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*


class HomeActivity : MainActivity(), FeedAdapter.Listener {

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var mAdapter: FeedAdapter
    private var likesListeners: Map<String, ValueEventListener> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(0)

        firebaseHelper  = FirebaseHelper(this)
        firebaseHelper.mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            mDatabaseRef.child("feed-posts")
                    .child(currentUser.uid)
                    .addValueEventListener(ValueEventListenerAdapter {
                        val posts = it.children.map { it.asFeedPost()!! }
                                .sortedByDescending { it.timeStampDate() }

                        mAdapter = FeedAdapter(this, posts)
                        feed_recycler.adapter = mAdapter
                        feed_recycler.layoutManager = LinearLayoutManager(this)
                    })
        }
    }

    override fun toggleLike(postId: String) {
        val likeReference = firebaseHelper.mDatabaseRef
            .child("likes")
            .child(postId)
            .child(firebaseHelper.currentUserUid()!!)

        likeReference
        .addListenerForSingleValueEvent(ValueEventListenerAdapter{
            if (it.exists()) likeReference.removeValue()
            else likeReference.setValue(true)
        })
        mAdapter.notifyDataSetChanged()
    }

    override fun loadLikes(postId: String, position: Int) {
        fun createListener() = firebaseHelper.mDatabaseRef
            .child("likes")
            .child(postId).addValueEventListener(ValueEventListenerAdapter {
                val userLikesId = it.children.map { it.key }.toSet()   // Set: O(1), List: O(n)
                val postLikes =
                    FeedPostLikes(
                        userLikesId.size,
                        userLikesId.contains(firebaseHelper.currentUserUid())
                    )   // check likes count and our like on current post

                mAdapter.updatePostLikes(position, postLikes)
            })
        if (likesListeners[postId] == null) likesListeners += (postId to createListener())
    }

    override fun onDestroy() {
        super.onDestroy()
        likesListeners.values.forEach{firebaseHelper.mDatabaseRef.removeEventListener(it)}
    }
}

class FeedAdapter(private val listener: Listener,
                  private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likeSet = postLikes[position] ?: FeedPostLikes(0, false)

        with(holder.view){
            feed_username.text = post.username
            feed_user_photo.loadUserPhoto(post.userPhoto)
            feed_image.loadImage(post.postImage)

            val quantityString = holder.view.context.resources.getQuantityString(R.plurals.likes_count, likeSet.likesCount, likeSet.likesCount)

            if (likeSet.likesCount == 0) {
                feed_like_text.visibility = View.INVISIBLE
            } else {
                feed_like_text.text = quantityString
            }

            setCaptionText(post)

            feed_like_ic.setOnClickListener {listener.toggleLike(post.id!!)}
            feed_like_ic.setImageResource(
                if (likeSet.likeByUser) R.drawable.ic_like_positive else R.drawable.ic_like_negative)
            listener.loadLikes(post.id!!, position)
        }
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    private fun View.setCaptionText(post: FeedPost) {

        val usernameSpannable = SpannableString(post.username)  //make username is bold
        usernameSpannable.setSpan(
            StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        usernameSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(widget.context, "click", Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {}

        }, 0, usernameSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        feed_caption_text.text = SpannableStringBuilder().append(usernameSpannable)
                .append(" ").append(post.caption)

        feed_caption_text.movementMethod = LinkMovementMethod.getInstance() //make clickable text
    }

    override fun getItemCount(): Int = posts.size
}

data class FeedPostLikes(val likesCount: Int, val likeByUser: Boolean)
