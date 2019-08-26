package com.example.chatty.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel(application: Application):AndroidViewModel(application) {

    lateinit var mAuth: FirebaseAuth
    val user = MutableLiveData<FirebaseUser>()
    val authMethod = MutableLiveData<AuthMethod>()

//    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestEmail()
//        .build()

//    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//    val account = GoogleSignIn.getLastSignedInAccount(this)
//    updateUI(account)

    fun init() {
        mAuth = FirebaseAuth.getInstance()
        user.postValue(mAuth.currentUser)
        authMethod.postValue(AuthMethod.SIGNIN)
    }

    fun signUp(name: String,email: String, password:String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val request = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    mAuth.currentUser?.updateProfile(request)
                    user.postValue(null)
                }
            }
    }

    fun login(email:String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    user.postValue(mAuth.currentUser)
                } else {
                    user.postValue(null)
                }

            }
    }

    fun passwordreset(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(getApplication(),"E-mail sent!",Toast.LENGTH_SHORT).show()
                }
            }

    }

}

enum class AuthMethod {
    SIGNIN,
    SIGNUP
}