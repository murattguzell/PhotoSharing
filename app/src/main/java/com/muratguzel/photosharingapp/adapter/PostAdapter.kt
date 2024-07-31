package com.muratguzel.photosharingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratguzel.photosharingapp.databinding.FeedRowBinding
import com.muratguzel.photosharingapp.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(val postList:ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostWH>() {
    class PostWH(val binding:FeedRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWH {
        val binding = FeedRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostWH(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostWH, position: Int) {
        holder.binding.recyclerUserName.text = postList.get(position).userName
        holder.binding.explanationText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)

    }
}