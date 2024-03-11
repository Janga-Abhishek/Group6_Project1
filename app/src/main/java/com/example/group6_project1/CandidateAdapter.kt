package com.example.group6_project1

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

class CandidateAdapter (options: FirebaseRecyclerOptions<Candidate>)  : FirebaseRecyclerAdapter<Candidate, CandidateAdapter.MyViewHolder>(options)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  MyViewHolder(inflater,parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Candidate) {
        Log.d("CandidateAdapter", "onBindViewHolder - position: $position, Name: ${model.Name}")

        holder.txtName.text=model.Name
        holder.txtEducation.text=model.Education
        val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.Photo)
        Glide.with(holder.imageView.context).load(storageReference).into(holder.imageView)
    }
    class MyViewHolder(inflater : LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.connect_candidate_main_row,parent,false))
    {
        val txtName : TextView =itemView.findViewById(R.id.user_name)
        val txtEducation : TextView =itemView.findViewById(R.id.education)
        val imageView: ImageView = itemView.findViewById(R.id.candidateImage)
    }
}