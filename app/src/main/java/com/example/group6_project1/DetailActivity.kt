package com.example.group6_project1

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {

    private lateinit var candidateImageDetail: ImageView
    private lateinit var connectBtn: Button
    private lateinit var userName: TextView
    private lateinit var jobDetail: TextView
    private lateinit var workExperienceDetail: TextView
    private lateinit var educationDetail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.candidate_details)

        candidateImageDetail = findViewById(R.id.candidateImageDetail)
        connectBtn = findViewById(R.id.connect_btn)
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

        val storageReference: StorageReference? = candidatePhoto?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it)
        }
        storageReference?.let {Glide.with(this).load(it).into(candidateImageDetail)
        }
    }
}
