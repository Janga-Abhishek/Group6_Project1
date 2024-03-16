package com.example.group6_project1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
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

        val showConnections: Button = findViewById(R.id.yourConnections)
        showConnections.setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
        val connectMoreMain: Button = findViewById(R.id.connect_more_main)
        connectMoreMain.setOnClickListener {
            val intent = Intent(this, CandidateActivity::class.java)
            startActivity(intent)
        }
        val createPost: Button = findViewById(R.id.writePost)
        createPost.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
        val logoutButton: Button = findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            auth?.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        auth = FirebaseAuth.getInstance()
        currentUserID = auth?.currentUser?.uid ?: ""

        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)

//        val intent = intent
//        val candidateID = intent.getStringExtra("candidateID")

        val query = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("FriendsPosts")
//        Log.d("Query", query.toString())
        val options = FirebaseRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        mainAdapter = MainAdapter(options)
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
