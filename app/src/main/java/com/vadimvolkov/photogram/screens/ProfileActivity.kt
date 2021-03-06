package com.vadimvolkov.photogram.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.addFriends.AddFriendActivity
import com.vadimvolkov.photogram.data.firebase.common.FirebaseHelper
import com.vadimvolkov.photogram.data.firebase.common.asUser
import com.vadimvolkov.photogram.editProfile.EditProfileActivity
import com.vadimvolkov.photogram.models.User
import com.vadimvolkov.photogram.screens.common.MainActivity
import com.vadimvolkov.photogram.common.ValueEventListenerAdapter
import com.vadimvolkov.photogram.screens.common.loadImage
import com.vadimvolkov.photogram.screens.common.loadUserPhoto
import com.vadimvolkov.photogram.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : MainActivity() {

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var mUser: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupBottomNavigation(4)

        firebaseHelper = FirebaseHelper(this)
        firebaseHelper.currentUserReference().addValueEventListener(
                ValueEventListenerAdapter {
                    mUser = it.asUser()!!
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

        firebaseHelper.mDatabaseRef.child("images").child(firebaseHelper.currentUserUid()!!)
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
}

class SquareImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}