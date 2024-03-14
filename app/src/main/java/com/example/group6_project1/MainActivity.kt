package com.example.group6_project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var mainAdapter: MainAdapter? = null
    private var currentUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.connectMore)
        registerButton.setOnClickListener {
            val intent = Intent(this, CandidateActivity::class.java)
            startActivity(intent)
        }
        auth = FirebaseAuth.getInstance()

        val logoutButton: Button = findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            auth?.signOut()

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        currentUserID = auth?.currentUser?.uid ?: ""
        val query = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends")
        val options = FirebaseRecyclerOptions.Builder<Candidate>().setQuery(query, Candidate::class.java).build()

        mainAdapter = MainAdapter(options)

        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = mainAdapter
    }
    override fun onStart() {
        super.onStart()
        mainAdapter?.startListening()
        mainAdapter?.notifyDataSetChanged()
    }
    override fun onStop() {
        super.onStop()
        mainAdapter?.stopListening()
    }
}