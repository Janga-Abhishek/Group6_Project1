package com.example.group6_project1

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions


class CandidateActivity : AppCompatActivity(){
    private var adapter: CandidateAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.candidate_list)

        val query = FirebaseDatabase.getInstance().reference.child("Candidates")
        val options =
            FirebaseRecyclerOptions.Builder<Candidate>().setQuery(query, Candidate::class.java).build()
        adapter = CandidateAdapter(options)
        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
    }
}