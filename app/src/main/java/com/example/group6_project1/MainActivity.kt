package com.example.group6_project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.connectMore)
        registerButton.setOnClickListener {
            val intent = Intent(this, CandidateActivity::class.java)
            startActivity(intent)
        }

        // logout
        auth = FirebaseAuth.getInstance()
        val logoutButton: Button = findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }


    }
}