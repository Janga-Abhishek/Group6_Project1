package com.example.group6_project1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var mainAdapter: MainAdapter? = null
    private var currentUserID: String = ""
    private var CandidateIds: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val showConnections: Button = findViewById(R.id.yourConnections)
        showConnections.setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
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
        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends")

        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val candidateIds = mutableListOf<String>()
                for (friendSnapshot in dataSnapshot.children) {
                    val candidateID = friendSnapshot.key
                    candidateID?.let { candidateIds.add(it) }
                    Log.d("CandidateIds", candidateIds.toString())


                    // Update this path to point to your posts
                    for (candidateId in candidateIds) {
                        val query = FirebaseDatabase.getInstance().reference
                            .child("Candidates")
                            .child(currentUserID)
                            .child("friends")
                            .child(candidateId)
                            .child("FriendsPosts")

                        val options = FirebaseRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
                        mainAdapter = MainAdapter(options)

                        Log.d("Query1", query.toString())



                }
                    val rView: RecyclerView = findViewById(R.id.rView)
                    rView.layoutManager = LinearLayoutManager(this@MainActivity)


                    rView.adapter = mainAdapter
            }}


            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("FirebaseError", "Error: ${databaseError.message}")
            }
        })

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
