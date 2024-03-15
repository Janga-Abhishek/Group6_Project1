package com.example.group6_project1

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class CandidateAdapter (options: FirebaseRecyclerOptions<Candidate>)  : FirebaseRecyclerAdapter<Candidate, CandidateAdapter.MyViewHolder>(options)
{
    private var currentUserID: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  MyViewHolder(inflater,parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Candidate) {
        Log.d("CandidateAdapter", "onBindViewHolder - position: $position, Name: ${model.Name}")

        holder.txtName.text=model.Name
        holder.txtJob.text=model.Job
        holder.txtEducation.text=model.Education
        holder.txtCompany.text=model.Company
//        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.Photo)
//        Glide.with(holder.imageView.context).load(storageReference).into(holder.imageView)

        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("profile_images/${model.Photo}")
        Glide.with(holder.imageView.context).load(storageReference).into(holder.imageView)


        var auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid ?: ""
        val candidateID = getRef(position).key
        val friendsReference =
            FirebaseDatabase.getInstance().reference.child("Candidates").child(currentUserID).child("friends").child("friendsList")

        friendsReference.child(candidateID!!).get().addOnSuccessListener {
            if (it.exists()) {
                holder.txtStatus.text = "Connected"
            } else {
                holder.txtStatus.text = ""
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("Name", model.Name)
            intent.putExtra("Job", model.Job)
            intent.putExtra("Photo", model.Photo)
            intent.putExtra("WorkExperience", model.Work_experience)
            intent.putExtra("Education", model.Education)
            intent.putExtra("CandidateID", getRef(position).key)

            holder.itemView.context.startActivity(intent)
        }
    }
    class MyViewHolder(inflater : LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.connect_candidate_main_row,parent,false))
    {
        val txtName : TextView =itemView.findViewById(R.id.user_name)
        val txtJob : TextView =itemView.findViewById(R.id.job_detail)
        val txtEducation: TextView =itemView.findViewById(R.id.education)
        val txtCompany: TextView =itemView.findViewById(R.id.company)
        val imageView: ImageView = itemView.findViewById(R.id.candidateImage)
        val txtStatus: TextView = itemView.findViewById(R.id.status)
    }
}