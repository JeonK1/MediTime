package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Model.NoticeInfo
import com.example.meditime.R
import com.example.meditime.Util.DowConverterFactory

class NoticeAdapter(val items: ArrayList<NoticeInfo>) :
    RecyclerView.Adapter<NoticeAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: NoticeAdapter.MyViewHolder, view: View, position: Int)
        fun OnCallBtnClick(holder: NoticeAdapter.MyViewHolder, view: View, position: Int)
        fun OnBellBtnClick(holder: NoticeAdapter.MyViewHolder, view: View, position: Int)
    }

    var itemClickListener: NoticeAdapter.OnItemClickListener?=null // 버튼클릭 listener

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medi_name: TextView = itemView.findViewById(R.id.tv_name)
        var medi_cycle: TextView = itemView.findViewById(R.id.tv_cycle)
        var medi_count:TextView = itemView.findViewById(R.id.tv_count)
        var medi_bell: ImageButton = itemView.findViewById(R.id.ib_bell)
        var medi_call: ImageButton = itemView.findViewById(R.id.ib_call)
        var bell_flag = false
        var call_flag = false

        init {
            medi_bell.setOnClickListener {
                itemClickListener?.OnBellBtnClick(this, it, adapterPosition)
            }
            medi_call.setOnClickListener {
                itemClickListener?.OnCallBtnClick(this, it, adapterPosition)
            }
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoticeAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notice_alarm, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NoticeAdapter.MyViewHolder, position: Int) {
        var name = items[position].medi_name
        var set_cycle = items[position].set_cycle
        var re_type = items[position].re_type
        var re_cycle = items[position].re_cycle
        var call_alart = items[position].call_alart
        var normal_alart = items[position].normal_alart
        var day_of_count = items[position].time_list.size

        holder.medi_name.text = name
        holder.medi_count.text = "1일 ${day_of_count}회 복용"
        if (call_alart == 0) {
            holder.medi_call.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
            holder.call_flag = false
        } else {
            holder.medi_call.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
            holder.call_flag = true
        }
        if (normal_alart == 0) {
            holder.medi_bell.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
            holder.bell_flag = false
        } else {
            holder.medi_bell.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
            holder.bell_flag = true
        }
        if(set_cycle==0) {
            holder.medi_cycle.text = "매일"
        }
        else{
            when {
                re_type==0 && re_cycle!=null -> {
                    // 요일 반복
                    val dayofweek_info = arrayListOf("일", "월", "화", "수", "목", "금", "토")
                    val dayofweek_flag = DowConverterFactory.convert_int_to_arrayList(re_cycle)
                    // 매주 무슨요일 반복인지 문자열 만들어주기
                    var tmp_str = "매주["
                    var cnt=0
                    for (i in 0 until 7){
                        if(dayofweek_flag[i]){
                            if(cnt++==0)
                                tmp_str += dayofweek_info[i]
                            else
                                tmp_str += ", ${dayofweek_info[i]}"
                        }
                    }
                    tmp_str+="]"
                    holder.medi_cycle.text = tmp_str
                }
                re_type==1 -> holder.medi_cycle.text = "${re_cycle}일 간격" // 일 반복
                re_type==2 -> holder.medi_cycle.text = "${re_cycle}개월 간격" // 개월 반복
            }
        }
    }

    fun getItems(position: Int): NoticeInfo {
        return items[position]
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
    }

    fun addItem(noticeInfo: NoticeInfo) {
        items.add(noticeInfo)
    }
}