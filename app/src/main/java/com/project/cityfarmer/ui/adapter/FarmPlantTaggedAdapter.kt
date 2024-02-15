package com.project.cityfarmer.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.cityfarmer.api.response.FarmPlantListResponse
import com.project.cityfarmer.api.response.FarmPlantTaggedListResponse
import com.project.cityfarmer.databinding.RowFarmBinding
import com.project.cityfarmer.ui.MainActivity
import java.util.Date
import java.util.concurrent.TimeUnit

class FarmPlantTaggedAdapter (var result: FarmPlantTaggedListResponse, var mainActivity: MainActivity) :
    RecyclerView.Adapter<FarmPlantTaggedAdapter.ViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

//    lateinit var mainActivity: MainActivity

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(mainActivity).load(result.data.get(position).mainImage).into(holder.plantImage)
        holder.plantName.text = result.data.get(position).nickname
        holder.plantType.text = result.data.get(position).plantTypeName
        var tagWateringDate = "오늘 물 줌"
        if (differDay(result.data.get(position).lastWateredAt) == 0) {
            tagWateringDate = "오늘 물 줌"
        } else {
            tagWateringDate =
                "${differDay(result.data.get(position).lastWateredAt).toString()}일 전 물 줌"
        }
        var tagRepottingDate = "오늘 분갈이 함"
        if (differDay(result.data.get(position).lastWateredAt) == 0) {
            tagRepottingDate = "오늘 분갈이 함"
        } else {
            tagRepottingDate =
                "${differDay(result.data.get(position).lastWateredAt).toString()}일 전 분갈이 함"
        }
        holder.tagWatering.text = tagWateringDate
        holder.tagRepotting.text = tagRepottingDate
        holder.date.text = "키운지 ${differDay(result.data.get(position).startAt)}일째"
    }

    override fun getItemCount() = result.data.size

    inner class ViewHolder(val binding: RowFarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val plantName = binding.textViewFarmPlantName
        val plantType = binding.textViewFarmPlantType
        val tagWatering = binding.textViewTagWatering
        val tagRepotting = binding.textViewTagRepotting
        val date = binding.textViewFarmDate
        val plantImage = binding.imageViewFarmPlant

        init {
            binding.root.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PLANT_DETAIL_FRAGMENT, true, null)
                true
            }
        }
    }

    fun differDay(inputDate: Date): Int {

        // 현재 날짜 가져오기
        val currentDate = Date()
        Log.d("##", "${currentDate}")

        val diffInMillis = currentDate.time - inputDate.time

        // 밀리초를 일(day)로 변환
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()

        Log.d("##", "날짜 차이 : ${diffInDays}일")

        return diffInDays
    }
}