package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Model.NoticeInfo
import com.example.meditime.Model.TodayInfo
import com.example.meditime.R
import com.example.meditime.Util.DowConverterFactory

class TodayAdapter(val items: ArrayList<TodayInfo>) :
    RecyclerView.Adapter<TodayAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: TodayAdapter.MyViewHolder, view: View, position: Int)
        fun OnCallBtnClick(holder: TodayAdapter.MyViewHolder, view: View, position: Int)
        fun OnBellBtnClick(holder: TodayAdapter.MyViewHolder, view: View, position: Int)
    }

    var itemClickListener: TodayAdapter.OnItemClickListener?=null // 버튼클릭 listener

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medi_name: TextView = itemView.findViewById(R.id.TodayItem_MediName)
        var AlarmTime: TextView = itemView.findViewById(R.id.TodayItem_SetTime)


//        init {
//            medi_bell.setOnClickListener {
//                itemClickListener?.OnBellBtnClick(this, it, adapterPosition)
//            }
//            medi_call.setOnClickListener {
//                itemClickListener?.OnCallBtnClick(this, it, adapterPosition)
//            }
//            itemView.setOnClickListener {
//                itemClickListener?.OnItemClick(this, it, adapterPosition)
//            }
//        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodayAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_alarm, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TodayAdapter.MyViewHolder, position: Int) {
        var name = items[position].medi_no
        var set_date = items[position].set_date

        holder.medi_name.text = name.toString()
        holder.AlarmTime.text = set_date
//        if (call_alart == 0) {
//            holder.medi_call.backgroundTintList =
//                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//            holder.call_flag = false
//        } else {
//            holder.medi_call.backgroundTintList =
//                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//            holder.call_flag = true
//        }
//        if (normal_alart == 0) {
//            holder.medi_bell.backgroundTintList =
//                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//            holder.bell_flag = false
//        } else {
//            holder.medi_bell.backgroundTintList =
//                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//            holder.bell_flag = true
//        }
//        if(set_cycle==0) {
//            holder.medi_cycle.text = "매일"
//        }
//        else{
//            when {
//                re_type==0 && re_cycle!=null -> {
//                    // 요일 반복
//                    val dayofweek_info = arrayListOf("일", "월", "화", "수", "목", "금", "토")
//                    val dayofweek_flag = DowConverterFactory.convert_int_to_arrayList(re_cycle)
//                    // 매주 무슨요일 반복인지 문자열 만들어주기
//                    var tmp_str = "매주["
//                    var cnt=0
//                    for (i in 0 until 7){
//                        if(dayofweek_flag[i]){
//                            if(cnt++==0)
//                                tmp_str += dayofweek_info[i]
//                            else
//                                tmp_str += ", ${dayofweek_info[i]}"
//                        }
//                    }
//                    tmp_str+="]"
//                    holder.medi_cycle.text = tmp_str
//                }
//                re_type==1 -> holder.medi_cycle.text = "${re_cycle}일 간격" // 일 반복
//                re_type==2 -> holder.medi_cycle.text = "${re_cycle}개월 간격" // 개월 반복
//            }
//        }
    }

    fun getItems(position: Int): TodayInfo {
        return items[position]
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
    }

    fun addItem(noticeInfo: TodayInfo) {
        items.add(noticeInfo)
    }
}