package com.project.cityfarmer.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.cityfarmer.api.response.FeedListResponse
import com.project.cityfarmer.databinding.RowFarmBinding
import com.project.cityfarmer.databinding.RowFeedBinding
import com.project.cityfarmer.databinding.RowFeedTagBinding
import com.project.cityfarmer.databinding.RowHomeTodoPlantBinding
import com.project.cityfarmer.ui.MainActivity
import com.project.cityfarmer.utils.MyApplication
import java.util.Date
import java.util.concurrent.TimeUnit

class FeedListAdapter (var result: FeedListResponse, var mainActivity: MainActivity) :
    RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

//    lateinit var mainActivity: MainActivity

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mainActivity).load(result.data.get(position).image).into(holder.image)
        holder.type.text = result.data.get(position).type
        holder.title.text = result.data.get(position).title
        holder.content.text = result.data.get(position).description
        holder.date.text = result.data.get(position).createdAt.toString()
        holder.like.text = result.data.get(position).likeCount.toString()
        holder.comment.text = result.data.get(position).commentCount.toString()
        holder.tagList.run {
            adapter = RecyclerViewAdapter(position)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    override fun getItemCount() = result.data.size

    inner class ViewHolder(val binding: RowFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val type = binding.textViewType
            val title = binding.textViewFeedTitle
            val content = binding.textViewFeedContent
            val tagList = binding.recyclerViewTag
            val image = binding.imageViewFeed
            val date = binding.textViewFeedDate
            val like = binding.textViewLikeNum
            val comment = binding.textViewCommentNum

        init {
            binding.root.setOnClickListener {
                MyApplication.feedId = result?.data!!.get(adapterPosition).id
                mainActivity.replaceFragment(MainActivity.PLANT_LOG_DETAIL_FRAGMENT, true, null)
                true
            }
        }
    }

    inner class RecyclerViewAdapter(var feedPosition: Int) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass>() {
        inner class ViewHolderClass(rowBinding: RowFeedTagBinding) :
            RecyclerView.ViewHolder(rowBinding.root) {

            val rowTag: TextView

            init {
                rowTag = rowBinding.textViewTag
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowPlantListBinding =
                RowFeedTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val viewHolder = ViewHolderClass(rowPlantListBinding)

            rowPlantListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolder
        }

        override fun getItemCount() = result.data.get(feedPosition).tags.size

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowTag.text =
                result.data.get(feedPosition).tags.get(position).toString()
        }
    }
}