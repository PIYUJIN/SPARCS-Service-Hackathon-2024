package com.project.cityfarmer.ui.adapter

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.cityfarmer.BuildConfig
import com.project.cityfarmer.R
import com.project.cityfarmer.api.ApiClient
import com.project.cityfarmer.api.TokenManager
import com.project.cityfarmer.api.request.SignUpRequest
import com.project.cityfarmer.api.response.CompleteTodoResponse
import com.project.cityfarmer.api.response.HomeListResponse
import com.project.cityfarmer.api.response.SignUpResponse
import com.project.cityfarmer.databinding.RowHomeTodoBinding
import com.project.cityfarmer.databinding.RowHomeTodoPlantBinding
import com.project.cityfarmer.ui.MainActivity
import com.project.cityfarmer.ui.OnboardingActivity
import com.project.cityfarmer.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeTodoAdapter(var result: HomeListResponse, var mainActivity: MainActivity) :
    RecyclerView.Adapter<HomeTodoAdapter.TodoViewHolder>() {
    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        context = parent.context
        val binding =
            RowHomeTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if(result.data[position].type == "watering") {
            holder.todo.text = "물주기"
            holder.deadline.text = "${changeDateFormat(result.data.get(position).tasks.get(0).deadline)}(오늘)까지 완료해야하는 작업이에요"
        } else if(result.data[position].type == "repotting") {
            holder.todo.text = "분갈이 하기"
            holder.deadline.text = "${changeDateFormat(result.data.get(position).tasks.get(0).deadline)}(오늘)까지 완료해야하는 작업이에요"
        } else if(result.data[position].type == "watering_complete") {
            holder.todo.text = "물주기(완료)"
            holder.deadline.text = "고생하셨어요!"
        } else if(result.data[position].type == "repotting_complete") {
            holder.todo.text = "분갈이 하기(완료)"
            holder.deadline.text = "고생하셨어요!"
        }
        holder.recyclerView.run {
            adapter = RecyclerViewAdapter(position)
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getItemCount() = result?.data!!.size

    inner class TodoViewHolder(val binding: RowHomeTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val deadline = binding.textViewDeadline
        val todo = binding.textViewTodo
        val recyclerView = binding.recyclerViewTodoPlantInfo
        val layout = binding.layoutTodo

        init {
            binding.root.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.PLANT_DETAIL_FRAGMENT, true, null)
                true
            }
        }
    }

    inner class RecyclerViewAdapter(var todoPosition: Int) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderClass>() {
        inner class ViewHolderClass(rowBinding: RowHomeTodoPlantBinding) :
            RecyclerView.ViewHolder(rowBinding.root) {

            val rowPlantImage: ImageView
            val rowDelayDate: TextView
            val rowPlantName: TextView
            val rowPlantType: TextView
            val rowChecked: ImageView

            init {
                rowPlantImage = rowBinding.imageViewPlant
                rowDelayDate = rowBinding.textViewDelay
                rowPlantName = rowBinding.textViewPlantName
                rowPlantType = rowBinding.textViewPlantType
                rowChecked = rowBinding.imageViewCheckBox

                rowBinding.root.setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.PLANT_DETAIL_FRAGMENT, true, null)
                }

                rowBinding.imageViewCheckBox.setOnClickListener {
                    if (result.data.get(todoPosition).type == "watering" || result.data.get(todoPosition).type == "repotting") {
                        completeTodo(result.data.get(todoPosition).tasks.get(adapterPosition).id)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
            val rowPlantListBinding =
                RowHomeTodoPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            val viewHolder = ViewHolderClass(rowPlantListBinding)

            rowPlantListBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return viewHolder
        }

        override fun getItemCount() = result.data.get(todoPosition).tasks.size

        override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
            if (result.data.get(todoPosition).type == "watering" || result.data.get(todoPosition).type == "repotting") {
                if (differDay(result.data.get(todoPosition).tasks.get(position).deadline) != 0) {
                    holder.rowDelayDate.text = "${differDay(result.data.get(todoPosition).tasks.get(position).deadline)}일 늦음"
                    holder.rowChecked.setImageResource(R.drawable.img_todo)
                }
            } else if (result.data.get(todoPosition).type == "watering_complete" || result.data.get(todoPosition).type == "repotting_complete") {
                holder.rowDelayDate.visibility = View.GONE
                holder.rowChecked.setImageResource(R.drawable.img_todo_checked)

            }
            Glide.with(mainActivity).load("${BuildConfig.server_url}${result.data.get(todoPosition).tasks.get(position).plant.mainImage}").into(holder.rowPlantImage)
            holder.rowPlantName.text =
                result.data.get(todoPosition).tasks.get(position).plant.nickname.toString()
            holder.rowPlantType.text =
                result.data.get(todoPosition).tasks.get(position).plant.plantTypeName.toString()
        }
    }

    fun completeTodo(id: Int) {
        var apiClient = ApiClient(mainActivity)

        var tokenManager = TokenManager(mainActivity)

        apiClient.apiService.checkTodo("Bearer ${tokenManager.getAccessToken().toString()}", id)?.enqueue(object :
            Callback<HomeListResponse> {
            override fun onResponse(
                call: Call<HomeListResponse>,
                response: Response<HomeListResponse>
            ) {
                if (response.isSuccessful) {
                    // 정상적으로 통신이 성공된 경우
                    var serverResponse: HomeListResponse? = response.body()
                    Log.d("##", "onResponse 성공: " + result?.toString())

                    result = serverResponse!!
                    notifyDataSetChanged()

                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    var result: HomeListResponse? = response.body()
                    Log.d("##", "onResponse 실패")
                    Log.d("##", "onResponse 실패: " + response.code())
                    Log.d("##", "onResponse 실패: " + response.body())
                    val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                    Log.d("##", "Error Response: $errorBody")
                }
            }

            override fun onFailure(call: Call<HomeListResponse>, t: Throwable) {
                // 통신 실패
                Log.d("##", "onFailure 에러: " + t.message.toString());
            }
        })
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

    fun changeDateFormat(inputDate: Date): String {
        // 날짜 문자열을 날짜 객체로 변환
        val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
        val inputDate = inputDateFormat.parse(inputDate.toString())

        // 출력 형식 지정
        val outputDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.US)

        // 날짜를 원하는 형식으로 변환
        val formattedDate = outputDateFormat.format(inputDate)

        Log.d("##", "${formattedDate}")

        return formattedDate
    }

//    fun getSize() : Int {
//        var num = 4
//        if(result?.watering?.size == 0) {
//            num = num -1
//        }
//        if(result?.repotting?.size == 0) {
//            num = num -1
//        }
//        if(result?.watering_complete?.size == 0) {
//            num = num -1
//        }
//        if(result?.repotting_complete?.size == 0) {
//            num = num -1
//        }
//
//        return num
//    }
}
