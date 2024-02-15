package com.project.cityfarmer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.cityfarmer.api.response.Comment
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.api.response.FeedDetailResponse
import com.project.cityfarmer.databinding.RowFarmBinding
import com.project.cityfarmer.databinding.RowFeedCommentBinding
import com.project.cityfarmer.ui.MainActivity

class FeedCommentAdapter (var result: List<Comment>) :
    RecyclerView.Adapter<FeedCommentAdapter.ViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFeedCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = result.get(position).username
        holder.commemt.text = result.get(position).description
    }

    override fun getItemCount() = result.size

    inner class ViewHolder(val binding: RowFeedCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val userName = binding.textViewUserName
        val commemt = binding.textViewComment

    }
}