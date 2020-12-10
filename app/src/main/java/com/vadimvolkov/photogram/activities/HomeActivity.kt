package com.vadimvolkov.photogram.activities

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.utils.FirebaseHelper
import com.vadimvolkov.photogram.utils.ValueEventListenerAdapter
import com.vadimvolkov.photogram.utils.loadImage
import com.vadimvolkov.photogram.utils.loadUserPhoto
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.feed_item.view.*


class HomeActivity : MainActivity(0) {

    private val TAG = "HomeActivity"

    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")

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
        val currentUser = firebaseHelper.mAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            firebaseHelper.mDatabaseRef.child("feed-posts")
                    .child(currentUser.uid)
                    .addValueEventListener(ValueEventListenerAdapter {
                        val posts = it.children.map { it.getValue(FeedPost::class.java)!! }
                                .sortedByDescending { it.timeStampDate() }

                        feed_recycler.adapter = FeedAdapter(posts)
                        feed_recycler.layoutManager = LinearLayoutManager(this)
                    })
        }
    }
}

class FeedAdapter(private val posts: List<FeedPost>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]

        with(holder.view){
            feed_username.text = post.username
            feed_user_photo.loadUserPhoto(post.userPhoto)
            feed_image.loadImage(post.postImage)

            if (post.likesCount == 0) {
               feed_like_text.visibility = View.GONE
            } else {
                feed_like_text.text = "${post.likesCount} likes"
            }

            setCaptionText(post)
        }
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