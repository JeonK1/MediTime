package com.example.meditime.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.model.TodayInfo
import com.example.meditime.R

class TodayAdapter(val items: ArrayList<TodayInfo>) :
    RecyclerView.Adapter<TodayAdapter.MyViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(holder: TodayAdapter.MyViewHolder, view: View, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null // 버튼클릭 listener
    lateinit var activity: Activity

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medi_name: TextView = itemView.findViewById(R.id.TodayItem_mediName)
        var alarmTime: TextView = itemView.findViewById(R.id.TodayItem_setTime)
        var takeCheck: TextView = itemView.findViewById(R.id.TodayItem_takeCheck)
        var medicine_color: FrameLayout = itemView.findViewById(R.id.TodayItem_mediColor)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(this, it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodayAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_alarm, parent, false)
        v.apply { clipToOutline = true }

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: TodayAdapter.MyViewHolder, position: Int) {
        val name = items[position].medi_name
        val setdateTime = items[position].record_date.split(" ")[1].split(":")
        var alarm_hour = setdateTime[0].toInt()
        val alarm_min = setdateTime[1].toInt()
        var alarm_am_pm = ""

        if (alarm_hour > 12) {
            alarm_am_pm = "오후"
            alarm_hour -= 12
        } else {
            alarm_am_pm = "오전"
        }

        if (items[position].take_date == null) {
            holder.takeCheck.text = "복용예정"
            holder.medicine_color.setBackgroundResource(R.drawable.btn_today_alarm_base)
        } else {
            holder.takeCheck.text = "복용완료"
            holder.medicine_color.setBackgroundResource(R.drawable.btn_today_alarm_ok)
        }

        holder.alarmTime.text = "$alarm_am_pm ${alarm_hour}:${"%02d".format(alarm_min)}"
        holder.medi_name.text = name
    }
}