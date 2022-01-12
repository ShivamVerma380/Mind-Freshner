package com.example.mindfreshner

import android.content.Intent
import android.net.sip.SipErrorCode.TIME_OUT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val TIME_OUT= 2000
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        supportActionBar?.hide()
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            Handler().postDelayed(Runnable {
                val i = Intent(this@LaunchActivity, MainActivity::class.java)
                startActivity(i)
                finish()
            }, TIME_OUT.toLong())
        }else{
            Handler().postDelayed(Runnable {
                val i = Intent(this@LaunchActivity, LoginActivity::class.java)
                startActivity(i)
                finish()
            }, TIME_OUT.toLong())
        }

    }
}