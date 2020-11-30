package com.vadimvolkov.photogram.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.utils.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener,
    View.OnClickListener {

    private val TAG = "LoginActivity"
    private lateinit var mAuth : FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d(TAG, "onCreate: ")

        KeyboardVisibilityEvent.setEventListener(this, this)

        coordinateBtnAndInputs(login_button, login_email, login_password)

        login_button.setOnClickListener(this)
        text_signUp.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            scroll_login.scrollTo(0,scroll_login.bottom)
            text_signUp.visibility = View.GONE
        } else {
            scroll_login.scrollTo(0,scroll_login.top)
            text_signUp.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.login_button -> {
                val email = login_email.text.toString()
                val password = login_password.text.toString()
                if (validate(email,password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(){
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error with mAuth signIn", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error with email or password", Toast.LENGTH_LONG)
                        .show()
                }
            }

            R.id.text_signUp -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }

    }

    private fun validate(email : String, password : String) =
        email.isNotEmpty() &&
                password.isNotEmpty()
}