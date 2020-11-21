package com.vadimvolkov.photogram.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_email.next_button
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {

    private val TAG = "RegisterActivity"
    
    private var mName : String? = null
    private var mPassword : String? = null
    private var mEmail : String? = null

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result?.signInMethods?.isEmpty() != false) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, NamePassFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(this, "This email already exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, it.exception!!.message!!, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this, "Please, enter email", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            mName = fullName
            mPassword = password
            if (mEmail != null) {
                println("mail = $mEmail, password = $mPassword")
                mAuth.createUserWithEmailAndPassword(mEmail!!, mPassword!!).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val user = makeUser(fullName, mEmail!!)
                        mRef.child("users").child(it.result!!.user!!.uid).setValue(user).addOnCompleteListener{
                            if (it.isSuccessful) {
                                startHomeActivity()
                            } else {
                                Log.e(TAG, "onRegister: Error with create user profile", )
                                Toast.makeText(this, "Trouble with registration", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        Log.e(TAG, "onRegister: Error with create user", )
                        Toast.makeText(this, "Trouble with sign in/ registration", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                Log.e(TAG, "onRegister: Email or Password is null")
                Toast.makeText(this,"Please enter data",Toast.LENGTH_SHORT).show()
                supportFragmentManager.popBackStack()
            }
        } else {
            Toast.makeText(this, "Please, enter name and password", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun makeUser(fullName: String, email : String) : User {
        val username = mUsername(fullName)
        return User(name = fullName, username = username, email = mEmail!!)
    }

    private fun mUsername(fullName: String) =
        fullName.toLowerCase().replace(" " ,".")

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}

class EmailFragment : Fragment() {

    interface Listener {
        fun onNext(email : String)
    }

    private lateinit var mContext : Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        next_button.setOnClickListener{
            val email = register_email.text.toString()
            mContext.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Listener
    }
}

class NamePassFragment : Fragment() {

    interface Listener {
        fun onRegister(name : String, password : String)
    }

    private lateinit var mContext : Listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_finish.setOnClickListener{
            val name = register_name.text.toString()
            val password = register_password.text.toString()
            mContext.onRegister(name, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Listener
    }
}