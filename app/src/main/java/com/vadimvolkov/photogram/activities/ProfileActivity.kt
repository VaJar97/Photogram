package com.vadimvolkov.photogram.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.utils.FirebaseHelper
import com.vadimvolkov.photogram.utils.ValueEventListenerAdapter
import com.vadimvolkov.photogram.utils.loadUserPhoto
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : MainActivity(4) {

    private val TAG = "ProfileActivity"
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var mUser: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation()

        firebaseHelper = FirebaseHelper(this)
        firebaseHelper.currentUserReference().addValueEventListener(ValueEventListenerAdapter {
            mUser = it.getValue(User::class.java)!!
            avatar.loadUserPhoto(mUser.photo)
            toolbar_text.text = mUser.username
        })


        profile_settings.setOnClickListener{
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }
        add_friend_image.setOnClickListener {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent)
        }

        edit_button.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        profile_recycler.layoutManager = GridLayoutManager(this, 3) // 3 - column

        firebaseHelper.mDatabaseRef.child("images").child(firebaseHelper.mAuth.currentUser!!.uid)
            .addValueEventListener(ValueEventListenerAdapter{
                val images = it.children.map{it.getValue(String::class.java)!!}
                profile_recycler.adapter = ImagesAdapter(images)
            })
    }
}

//  RecyclerView -> LayoutManager -> Adapter (ViewHolder for optimizations)
class ImagesAdapter(private val images: List<String>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imageView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    override fun getItemCount(): Int = images.size

    private fun ImageView.loadImage(image: String) {
        Glide.with(this).load(image).centerCrop().into(this)
    }
}

class SquareImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}