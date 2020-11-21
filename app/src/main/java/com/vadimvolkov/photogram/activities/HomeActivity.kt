package com.vadimvolkov.photogram.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.vadimvolkov.photogram.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : MainActivity(0) {

    private val TAG = "HomeActivity"

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation()
        Log.d(TAG, "onCreate: ")

        mAuth = FirebaseAuth.getInstance()
//        val email = "vadim@volkov.com"
//        val password = "password"
//        mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener{
//                if (it.isSuccessful) {
//                    Log.d(TAG, "onCreate: Sign In successful")
//                } else {
//                    Log.e(TAG, "onCreate: Sign in error", )
//                }
//            }

        home_text.setOnClickListener(View.OnClickListener {
            mAuth.signOut()
        })

        mAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}