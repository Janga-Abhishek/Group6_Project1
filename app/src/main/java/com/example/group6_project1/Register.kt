package com.example.group6_project1
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Register : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var selectedImageUri: String = ""
    private var profileImage: String = ""

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                selectedImageUri = imageUri.toString()
                Glide.with(this).load(imageUri).into(findViewById<ImageView>(R.id.register_image_view))
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference

        val btnChooseImage = findViewById<Button>(R.id.button_choose_image)
        btnChooseImage.setOnClickListener {
            selectCandidateImage()
        }
        val btnRegister = findViewById<Button>(R.id.button_register)
        btnRegister.setOnClickListener {
            registerUser()
        }
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
    private fun selectCandidateImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        getContent.launch(intent)
    }
    private fun registerUser() {
        val emailEditText = findViewById<EditText>(R.id.register_email)
        val passwordEditText = findViewById<EditText>(R.id.register_password)
        val nameEditText = findViewById<EditText>(R.id.register_name)
        val companyEditText=findViewById<EditText>(R.id.register_company_name)
        val jobEditText=findViewById<EditText>(R.id.register_job_role)
        val educationEditText=findViewById<EditText>(R.id.register_education)
        val workExperienceEditText=findViewById<EditText>(R.id.register_workExperience)

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val company=companyEditText.text.toString().trim()
        val job= jobEditText.text.toString().trim()
        val education = educationEditText.text.toString().trim()
        val workExperience= workExperienceEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || company.isEmpty() || job.isEmpty() || education.isEmpty() || workExperience.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth?.currentUser?.uid ?: ""
                    profileImage = "$userId.jpg"
                    saveUserDataToDatabase(userId, name, email, company, job, education, workExperience, profileImage)
                    uploadProfileImage(userId)
                    Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    private fun saveUserDataToDatabase(userId: String, name: String, email: String, company: String, job: String, education: String, workExperience: String, profileImage: String ) {
        val userRef = database?.reference?.child("Candidates")?.child(userId)
        userRef?.child("Name")?.setValue(name)
        userRef?.child("Email")?.setValue(email)
        userRef?.child("Company")?.setValue(company)
        userRef?.child("Job")?.setValue(job)
        userRef?.child("Education")?.setValue(education)
        userRef?.child("Work_experience")?.setValue(workExperience)
        userRef?.child("Photo")?.setValue(profileImage)
    }
    private fun uploadProfileImage(userId: String) {
        val profileImage = storageReference?.child("profile_images")?.child("$userId.jpg")
        val uploadTask = profileImage?.putFile(selectedImageUri.toUri())
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            profileImage.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
            }
        }
    }
}
