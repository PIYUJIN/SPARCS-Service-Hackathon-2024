package com.project.cityfarmer.ui

import android.media.session.MediaSession.Token
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.cityfarmer.BuildConfig
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.FeedCommentRequest
import com.project.cityfarmer.api.request.LikeFeedRequest
import com.project.cityfarmer.api.response.FeedCommentResponse
import com.project.cityfarmer.api.response.FeedDetailResponse
import com.project.cityfarmer.api.response.FeedListResponse
import com.project.cityfarmer.api.response.LikeFeedResponse
import com.project.cityfarmer.databinding.FragmentPlantLogDetailBinding
import com.project.cityfarmer.databinding.RowFeedTagBinding
import com.project.cityfarmer.ui.adapter.FeedCommentAdapter
import com.project.cityfarmer.ui.adapter.FeedListAdapter
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantLogDetailFragment : Fragment() {

    lateinit var fragmentPlantLogDetailBinding: FragmentPlantLogDetailBinding
    lateinit var mainActivity: MainActivity

    var userId = 0
    var isLiked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPlantLogDetailBinding = FragmentPlantLogDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentPlantLogDetailBinding.run {
            buttonBack.setOnClickListener {
                mainActivity.removeFragment(MainActivity.PLANT_LOG_DETAIL_FRAGMENT)
            }
            buttonSendComment.setOnClickListener {
                addComment(MyApplication.feedId)
            }
            textViewPlantCount.setOnClickListener {
                MyApplication.feedUserId = userId
                mainActivity.replaceFragment(MainActivity.FARM_DETAIL_FRAGMENT, true, null)
            }
            layoutTaggedPlant.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.TAGGED_DETAIL_FRAGMNET, true, null)
            }
            imageViewFeedLike.setOnClickListener {
                likeFeed()
            }
        }

        getFeedDetail(MyApplication.feedId)
        return fragmentPlantLogDetailBinding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavigation(true)
    }

    fun getFeedDetail(id: Int) {
        var apiClient = ApiClient(requireContext())
        var tokenManager = TokenManager(requireContext())

        apiClient.apiService.getFeedDetail("Bearer ${tokenManager.getAccessToken().toString()}", id)?.enqueue(object :
            Callback<FeedDetailResponse> {
            override fun onResponse(call: Call<FeedDetailResponse>, response: Response<FeedDetailResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: FeedDetailResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentPlantLogDetailBinding.run {
                        userId = result?.userId!!
                        textViewType.text = result?.type
                        textViewFeedTitle.text = result?.title
                        textViewFeedContent.text = result?.description
                        if(result?.type == "농업일지") {
                            if(result?.image == "") {
                                imageViewFeed.visibility = View.GONE
                                imageViewFeedDetailFarm.visibility = View.GONE
                            } else {
                                Glide.with(requireContext())
                                    .load("${BuildConfig.server_url}${result?.image}")
                                    .into(imageViewFeed)
                                imageViewFeedDetailFarm.visibility = View.GONE
                            }
                        } else {
                            if(result?.image == "") {
                                imageViewFeedDetailFarm.visibility = View.GONE
                                imageViewFeed.visibility = View.GONE
                            } else {
                                Glide.with(requireContext())
                                    .load("${BuildConfig.server_url}${result?.image}")
                                    .into(imageViewFeedDetailFarm)
                                imageViewFeed.visibility = View.GONE
                            }
                        }
                        textViewFeedUserName.text = result?.username
                        textViewPlantCount.text = "키우는 작물 ${result?.myFarmItemCount}개(밭자랑 보기)"
                        textViewLikeNum.text = result?.likeCount.toString()
                        textViewCommentNum.text = result?.commentCount.toString()
                        textViewTagPlantCount.text = "${result?.taggedItemCount.toString()}개"

                        if(result?.isLiked == true) {
                            imageViewFeedLike.setImageResource(R.drawable.like_filled)
                        } else {
                            imageViewFeedLike.setImageResource(R.drawable.like)
                        }
                    }

                    fragmentPlantLogDetailBinding.run {
                        recyclerViewTag.run {
                            adapter = RecyclerViewAdapter(result!!)
                            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        }
                        recyclerViewFeedComment.run {
                            adapter = FeedCommentAdapter(result!!.comments)
                            layoutManager = LinearLayoutManager(context)
                        }
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: FeedDetailResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<FeedDetailResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun addComment(id: Int) {
        var apiClient = ApiClient(requireContext())
        var tokenManager = TokenManager(requireContext())

        var Comment = FeedCommentRequest(id, fragmentPlantLogDetailBinding.editTextComment.text.toString())

        apiClient.apiService.addComment("Bearer ${tokenManager.getAccessToken().toString()}",Comment)?.enqueue(object :
            Callback<FeedCommentResponse> {
            override fun onResponse(call: Call<FeedCommentResponse>, response: Response<FeedCommentResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: FeedCommentResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentPlantLogDetailBinding.run {
                        recyclerViewFeedComment.run {
                            adapter = FeedCommentAdapter(result!!.data)
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        editTextComment.setText("")
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: FeedCommentResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<FeedCommentResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    fun likeFeed() {
        var apiClient = ApiClient(requireContext())
        var tokenManager = TokenManager(requireContext())

        var Diary = LikeFeedRequest(MyApplication.feedId)

        apiClient.apiService.likeFeed("Bearer ${tokenManager.getAccessToken().toString()}", Diary)?.enqueue(object :
            Callback<LikeFeedResponse> {
            override fun onResponse(call: Call<LikeFeedResponse>, response: Response<LikeFeedResponse>) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var result: LikeFeedResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    fragmentPlantLogDetailBinding.run {
                        isLiked = result?.is_like!!
                        textViewLikeNum.text = result?.like_count.toString()
                        if(isLiked) {
                            imageViewFeedLike.setImageResource(R.drawable.like_filled)
                        } else {
                            imageViewFeedLike.setImageResource(R.drawable.like)
                        }
                    }

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: LikeFeedResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<LikeFeedResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
    }

    inner class RecyclerViewAdapter(var result: FeedDetailResponse) :
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
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolder
        }

        override fun getItemCount() = result.tags.size

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            holder.rowTag.text =
                result.tags.get(position).toString()
        }
    }
}