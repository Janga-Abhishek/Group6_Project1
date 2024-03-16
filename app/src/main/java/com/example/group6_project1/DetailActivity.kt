package com.example.group6_project1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {

    private var candidateImageDetail: ImageView? = null
    private var connectBtn: Button? = null
    private var removeFriendBtn: Button? = null
    private var userName: TextView? = null
    private var jobDetail: TextView? = null
    private var workExperienceDetail: TextView? = null
    private var educationDetail: TextView? = null
    private var currentUserID: String = ""
    private var candidateID: String = ""
    private var preferences: SharedPreferences? = null
    private var detailAdapter: DetailAdapter? = null
    private var noOfFriends: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.candidate_details)

        candidateImageDetail = findViewById(R.id.candidateImageDetail)
        connectBtn = findViewById(R.id.connect_btn)
        removeFriendBtn = findViewById(R.id.remove_friend_btn)
        userName = findViewById(R.id.user_name)
        jobDetail = findViewById(R.id.job_detail)
        workExperienceDetail = findViewById(R.id.work_experience_detail)
        educationDetail = findViewById(R.id.education_detail)
        noOfFriends = findViewById(R.id.friend_count)

        val candidateName = intent.getStringExtra("Name")
        val candidateJob = intent.getStringExtra("Job")
        val candidatePhoto = intent.getStringExtra("Photo")
        val candidateWorkExperience = intent.getStringExtra("WorkExperience")
        val candidateEducation = intent.getStringExtra("Education")

        userName?.text = candidateName
        jobDetail?.text = candidateJob
        workExperienceDetail?.text = candidateWorkExperience
        educationDetail?.text = candidateEducation

        val storageReference: StorageReference? = candidatePhoto?.let { FirebaseStorage.getInstance().getReference("profile_images/$it") }
        storageReference?.let { Glide.with(this).load(it).into(candidateImageDetail!!) }


        val auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid ?: ""
        candidateID = intent.getStringExtra("CandidateID") ?: ""
        preferences = getPreferences(MODE_PRIVATE)

        connectBtn?.setOnClickListener {
            addCandidateToFriends()
        }

        removeFriendBtn?.setOnClickListener {
            removeFriend()
        }
        val connectionsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("friendsList")
        connectionsRef.child(candidateID).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {

                connectBtn?.text = "Connected"
                connectBtn?.isEnabled = false
            } else {
                connectBtn?.text = "Connect"
                connectBtn?.isEnabled = true
                removeFriendBtn?.text = "Remove Friend"
                removeFriendBtn?.isEnabled = false
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to check connection status: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
//        val connectionStatus = preferences?.getBoolean("Connection_$candidateID", false)
//        if (connectionStatus == true) {
//            connectBtn?.text = "Connected"
//            connectBtn?.isEnabled = false
//        } else {
//            removeFriendBtn?.text = "Remove Friend"
//            removeFriendBtn?.isEnabled = false
//        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
//        val postQuery = FirebaseDatabase.getInstance().reference.child(candidatePostsPath ?: "").child(currentUserID)
//        val postOptions = FirebaseRecyclerOptions.Builder<Post>()
//            .setQuery(postQuery, Post::class.java)
//            .build()
//        detailAdapter = DetailAdapter(postOptions)
//
//        val rView: RecyclerView = findViewById(R.id.rView)
//        rView.layoutManager = LinearLayoutManager(this)
//        rView.adapter = detailAdapter


        val currentUserFriendsRef = FirebaseDatabase.getInstance().reference
            .child("Candidates")
            .child(candidateID)
            .child("friends")
            .child("friendsList")

        currentUserFriendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val numberOfFriends = dataSnapshot.childrenCount.toInt()
                // Update UI or perform any action with the number of friends
                noOfFriends?.text = numberOfFriends.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.e("FriendsCount", "Failed to get friends count: ${databaseError.message}")
            }
        })
        val query = FirebaseDatabase.getInstance().reference.child("Candidates").child(candidateID).child("Posts")
        Log.d("Query", query.toString())

        val options = FirebaseRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        detailAdapter = DetailAdapter(options)

        val rView: RecyclerView = findViewById(R.id.rView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = detailAdapter
    }

    private fun addCandidateToFriends() {
        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("friendsList")

        val candidateDetails = HashMap<String, Any>()
        candidateDetails["Name"] = userName?.text.toString()
        candidateDetails["Job"] = jobDetail?.text.toString()
        candidateDetails["WorkExperience"] = workExperienceDetail?.text.toString()
        candidateDetails["Education"] = educationDetail?.text.toString()
        candidateDetails["Photo"] = intent.getStringExtra("Photo") ?: ""

        friendsRef.child(candidateID).setValue(candidateDetails)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences?.edit()?.putBoolean("Connection_$candidateID", true)?.apply()
                    connectBtn?.text = "Connected"
                    connectBtn?.isEnabled = false
                    removeFriendBtn?.text = "Remove Friend"
                    removeFriendBtn?.isEnabled = true

                    val postsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("FriendsPosts")
                    val query = FirebaseDatabase.getInstance().reference.child("Candidates").child(candidateID).child("Posts")
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (postSnapshot in dataSnapshot.children) {
                                val postId = postSnapshot.key
                                val post = postSnapshot.getValue(Post::class.java)
                                if (postId != null && post != null) {
                                    postsRef.child(postId).setValue(post)
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error
                        }
                    })
                } else {
                    Toast.makeText(this, "Failed to connect. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("candidateID", candidateID)
        startActivity(intent)
    }


//    private fun addCandidateToFriends() {
//        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends")
//
//        val candidateDetails = HashMap<String, Any>()
//        candidateDetails["Name"] = userName?.text.toString()
//        candidateDetails["Job"] = jobDetail?.text.toString()
//        candidateDetails["WorkExperience"] = workExperienceDetail?.text.toString()
//        candidateDetails["Education"] = educationDetail?.text.toString()
//        candidateDetails["Photo"] = intent.getStringExtra("Photo") ?: ""
//
//
//        friendsRef.child(candidateID).setValue(candidateDetails)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    preferences?.edit()?.putBoolean("Connection_$candidateID", true)?.apply()
//                    connectBtn?.text = "Connected"
//                    connectBtn?.isEnabled = false
//                    removeFriendBtn?.text = "Remove Friend"
//                    removeFriendBtn?.isEnabled = true
//                } else {
//                    Toast.makeText(this, "Failed to connect. Please try again.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun removeFriend() {
        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("friendsList")
        friendsRef.child(candidateID).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences?.edit()?.putBoolean("Connection_$candidateID", false)?.apply()
                    connectBtn?.text = "Connect"
                    connectBtn?.isEnabled = true
                    removeFriendBtn?.text = "Remove Friend"
                    removeFriendBtn?.isEnabled = false

                    val friendsPostsRef = friendsRef.child("FriendsPosts")
                    friendsPostsRef.child(candidateID).removeValue()
                        .addOnCompleteListener { postTask ->
                            if (postTask.isSuccessful) {
                                val query = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("FriendsPosts")
                                query.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (postSnapshot in dataSnapshot.children) {
                                            val postId = postSnapshot.key
                                            val postRef = query.child(postId!!)
                                            Log.d("PostRef", postRef.toString())
                                            val candidateIdValue = postSnapshot.child("candidateId").getValue(String::class.java)
                                            if (candidateIdValue == candidateID) {
                                                postRef.removeValue()
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Handle error
                                    }
                                })
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("candidateID", candidateID)
                                startActivity(intent)
                            } else {
                                // Handle failure to remove friend's posts
                                Log.e("RemoveFriendPosts", "Failed to remove friend's posts: ${postTask.exception}")
                            }
                        }
                } else {
                    Toast.makeText(this, "Failed to remove friend. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onStart() {
        super.onStart()
        detailAdapter?.startListening()
        detailAdapter?.notifyDataSetChanged()

    }

    override fun onStop() {
        super.onStop()
        detailAdapter?.stopListening()
    }
}
