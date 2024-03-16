package com.example.group6_project1

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

class DetailAdapter(options: FirebaseRecyclerOptions<Post>) :
    FirebaseRecyclerAdapter<Post, DetailAdapter.MyViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Post) {
        holder.txtContent.text = model.Content
        holder.txtName.text = model.CandidateName

        val storageReference: StorageReference =
            FirebaseStorage.getInstance().getReference("post_images/${model.Photo}")
        Glide.with(holder.imageView.context).load(storageReference).into(holder.imageView)
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.post_row, parent, false)) {
        val txtName: TextView = itemView.findViewById(R.id.candidateName_post_row)
        val imageView: ImageView = itemView.findViewById(R.id.imageView_post_row)
        val txtContent: TextView = itemView.findViewById(R.id.content_post_row)
    }
}
