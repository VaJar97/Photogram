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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vadimvolkov.photogram.R
import com.vadimvolkov.photogram.models.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_email.next_button
import kotlinx.android.synthetic.main.fragment_register_namepass.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener,
        NamePassFragment.Listener, KeyboardVisibilityEventListener {

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

        KeyboardVisibilityEvent.setEventListener(this, this)
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email) {signInMethods ->
                    if (signInMethods.isEmpty())  {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.frame_layout, NamePassFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(this, "This email already exist", Toast.LENGTH_SHORT)
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
                mAuth.createUserWithEmailAndPassword(mEmail!!, mPassword!!) {
                    val user = makeUser(fullName, mEmail!!)
                    mRef.createUser(it.user!!.uid, user) {
                        startHomeActivity()
                        }
                }
            } else {
                Toast.makeText(this,"Please, back and enter email",Toast.LENGTH_SHORT).show()
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

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            scroll_register.scrollTo(0,scroll_register.bottom)
        } else {
            scroll_register.scrollTo(0,scroll_register.top)
        }
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {
        child("users").child(uid).setValue(user).addOnCompleteListener{
            if (it.isSuccessful) {
                onSuccess()
            } else {
                Log.e(TAG, "onRegister: Error with create user profile", )
                Toast.makeText(this@RegisterActivity, "Trouble with registration", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String,
                                                            onSuccess: (AuthResult) -> Unit) {
        createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if (it.isSuccessful) {
                onSuccess(it.result!!)
            } else {
                Log.e(TAG, "onRegister: Error with create user", )
                Toast.makeText(this@RegisterActivity, "Trouble with sign in/ registration", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String, onSuccess: (List<String>) -> Unit) {
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.signInMethods?.let { it1 -> onSuccess(it1) }
            } else {
                Toast.makeText(this@RegisterActivity, it.exception!!.message!!, Toast.LENGTH_SHORT)
                        .show()
            }
        }
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
        coordinateBtnAndInputs(next_button, register_email)
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
        coordinateBtnAndInputs(button_finish, register_name, register_password)
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