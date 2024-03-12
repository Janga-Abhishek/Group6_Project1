package com.example.group6_project1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.content.SharedPreferences

class DetailActivity : AppCompatActivity() {

    private lateinit var candidateImageDetail: ImageView
    private lateinit var connectBtn: Button
    private lateinit var removeFriendBtn: Button
    private lateinit var userName: TextView
    private lateinit var jobDetail: TextView
    private lateinit var workExperienceDetail: TextView
    private lateinit var educationDetail: TextView

    private lateinit var currentUserID: String
    private lateinit var candidateID: String
    private lateinit var preferences: SharedPreferences

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

        val candidateName = intent.getStringExtra("Name")
        val candidateJob = intent.getStringExtra("Job")
        val candidatePhoto = intent.getStringExtra("Photo")
        val candidateWorkExperience = intent.getStringExtra("WorkExperience")
        val candidateEducation = intent.getStringExtra("Education")

        userName.text = candidateName
        jobDetail.text = candidateJob
        workExperienceDetail.text = candidateWorkExperience
        educationDetail.text = candidateEducation

        val storageReference: StorageReference? = candidatePhoto?.let { FirebaseStorage.getInstance().getReference("profile_images/$it")
        }
        storageReference?.let {Glide.with(this).load(it).into(candidateImageDetail)
        }

        var auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid ?: ""
        candidateID = intent.getStringExtra("CandidateID") ?: ""
        preferences = getPreferences(MODE_PRIVATE)

        connectBtn.setOnClickListener {
            addCandidateToFriends()
        }

        removeFriendBtn.setOnClickListener {
            removeFriend()
        }

        val connectionStatus = preferences.getBoolean("Connection_$candidateID", false)
        if (connectionStatus) {
            connectBtn.text = "Connected"
            connectBtn.isEnabled = false
        } else {
            removeFriendBtn.text = "Remove Friend"
            removeFriendBtn.isEnabled = false
        }
    }

    private fun addCandidateToFriends() {
        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends")

        val candidateDetails = HashMap<String, Any>()
        candidateDetails["Name"] = userName.text.toString()
        candidateDetails["Job"] = jobDetail.text.toString()
        candidateDetails["WorkExperience"] = workExperienceDetail.text.toString()
        candidateDetails["Education"] = educationDetail.text.toString()
        candidateDetails["Photo"] = intent.getStringExtra("Photo") ?: ""

        friendsRef.child(candidateID).setValue(candidateDetails)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update the connection status in SharedPreferences
                    preferences.edit().putBoolean("Connection_$candidateID", true).apply()

                    // Update UI
                    connectBtn.text = "Connected"
                    connectBtn.isEnabled = false
                    removeFriendBtn.text = "Remove Friend"
                    removeFriendBtn.isEnabled = true
                } else {
                    Toast.makeText(this, "Failed to connect. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun removeFriend() {
        val friendsRef = FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends")
        friendsRef.child(candidateID).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    preferences.edit().putBoolean("Connection_$candidateID", false).apply()
                    connectBtn.text = "Connect"
                    connectBtn.isEnabled = true
                    removeFriendBtn.text = "Remove Friend"
                    removeFriendBtn.isEnabled = false
                } else {
                    Toast.makeText(this, "Failed to remove friend. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
