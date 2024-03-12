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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainAdapter(options: FirebaseRecyclerOptions<Candidate>) :
    FirebaseRecyclerAdapter<Candidate, MainAdapter.MyViewHolder>(options) {
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
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.candidate_row,parent,false))
    {
        val txtName : TextView =itemView.findViewById(R.id.user_name)
        val txtJob : TextView =itemView.findViewById(R.id.job_detail)
        val txtEducation: TextView =itemView.findViewById(R.id.education)
        val txtCompany: TextView =itemView.findViewById(R.id.company)
        val imageView: ImageView = itemView.findViewById(R.id.candidateImage)
    }
}
