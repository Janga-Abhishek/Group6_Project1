package com.example.group6_project1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.log

class CreatePostActivity : AppCompatActivity() {
    private var selectedImageUri: String = ""
    private var auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    private var storage: FirebaseStorage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        val chooseImage=findViewById<Button>(R.id.button_choose_image_post)
         chooseImage.setOnClickListener {
            selectPostImage()
        }
        val postButton=findViewById<Button>(R.id.button_post)
        postButton.setOnClickListener {
            val userId = auth?.currentUser?.uid ?: ""
            val postText = findViewById<EditText>(R.id.editTextText).text.toString().trim()
            if (postText.isNotEmpty()) {
                getCandidateName(userId) { userName ->
                    uploadPost(userId, userName, postText)
                }
            } else {
                Toast.makeText(this, "Please write something to post", Toast.LENGTH_SHORT).show()
            }}
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
    private fun selectPostImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getContent.launch(intent)
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                selectedImageUri = imageUri.toString()
                Glide.with(this).load(imageUri).into(findViewById<ImageView>(R.id.post_image_view))
            }
        }
    private fun getCandidateName(userId: String, callback: (String) -> Unit) {
        database?.child("Candidates")?.child(userId)?.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("Name").value.toString()
                callback(userName)
            }
            override fun onCancelled(error: DatabaseError) {
                callback("Anonymous")
            }
        })
    }
    private fun uploadPost(userId: String, userName: String, postText: String) {
        val postId = database?.child("Candidates")?.child(userId)?.child("Posts")?.push()?.key ?: ""
        val postRef = database?.child("Candidates")?.child(userId)?.child("Posts")?.child(postId)
        postRef?.child("Content")?.setValue(postText)
        postRef?.child("CandidateId")?.setValue(userId)
        postRef?.child("CandidateName")?.setValue(userName)
        if (selectedImageUri.isNotEmpty()) {
            val imageName = "$postId.jpg"
            val storageRef = storage?.reference?.child("post_images")?.child(imageName)
            val uploadTask = storageRef?.putFile(selectedImageUri.toUri())
            uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                postRef?.child("Photo")?.setValue(imageName) // Store the path of the StorageReference
                storageRef.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                  Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to upload post", Toast.LENGTH_SHORT).show()
                }
            }} else {
            Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
