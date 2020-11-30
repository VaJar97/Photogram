package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

        edit_button.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        })
    }
}