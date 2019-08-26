package com.example.chatty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.widget.Toast
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chatty.viewmodel.AuthMethod
import com.example.chatty.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.*





class AuthActivity : AppCompatActivity() {

    lateinit var viewmodel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportActionBar?.setLogo(R.mipmap.chatty_icon_trans)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewmodel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        viewmodel.init()

        viewmodel.user.observe(this@AuthActivity, Observer {
            if (it == null) {
                return@Observer
            } else {
                val intent = Intent(this, ChatsActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        viewmodel.authMethod.observe(this@AuthActivity, Observer {
            if (it==null) {
                return@Observer
            }
            else
                updateUI(it)
        })

//        signup_button.setOnClickListener {
//            val email:String = email_textview.text.toString()
//            val password:String = password_textview.text.toString()
//            signUp(email,password)
//        }

    }


    fun updateUI(authMethod: AuthMethod){
        when(authMethod) {
            AuthMethod.SIGNIN -> {showLogin()}
            AuthMethod.SIGNUP -> {showSignup()}
        }
    }

    fun showLogin() {
        name_textview.visibility = View.GONE
        fp_textview.visibility = View.VISIBLE
        auth_button.text="LOGIN"
        authmodetoggler.text = "Don't have an account? Sign Up now!"
        auth_button.setOnClickListener {
            val email:String = email_textview.text.toString()
            val password:String = password_textview.text.toString()
            if(!email.isEmpty()&&!password.isEmpty())
                viewmodel.login(email,password)
            else
                Toast.makeText(getApplication(),"E-mail/Password incorrect",Toast.LENGTH_SHORT).show()
        }
        fp_textview.setOnClickListener {
            val email:String = email_textview.text.toString()
            if(!email.isEmpty())
                viewmodel.passwordreset(email)
            else
                Toast.makeText(getApplication(),"Invalid E-mail",Toast.LENGTH_SHORT).show()

        }
        authmodetoggler.setOnClickListener { viewmodel.authMethod.postValue(AuthMethod.SIGNUP) }

    }

    fun showSignup() {
        name_textview.visibility = View.VISIBLE
        fp_textview.visibility = View.GONE
        auth_button.text="SIGN UP"
        authmodetoggler.text = "ALready have an account? Login instead"
        auth_button.setOnClickListener {
            val name:String = name_textview.text.toString()
            val email:String = email_textview.text.toString()
            val password:String = password_textview.text.toString()
            if(!name.isEmpty()&&!email.isEmpty()&&!password.isEmpty()){
                viewmodel.signUp(name,email,password)
                Toast.makeText(application,"Signup Successful!",Toast.LENGTH_SHORT).show()
                name_textview.setText("")
                email_textview.setText("")
                password_textview.setText("")
                viewmodel.authMethod.postValue(AuthMethod.SIGNIN)

            }
            else
                Toast.makeText(application,"Invalid input. Try Again!",Toast.LENGTH_SHORT).show()

        }
        authmodetoggler.setOnClickListener { viewmodel.authMethod.postValue(AuthMethod.SIGNIN) }

    }

}
