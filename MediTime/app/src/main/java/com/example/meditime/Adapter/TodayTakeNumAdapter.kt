package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Model.NoticeAlarmInfo
import com.example.meditime.Model.TodayTimeInfo
import com.example.meditime.R

class TodayTakeNumAdapter(val items:ArrayList<TodayTimeInfo>): RecyclerView.Adapter<TodayTakeNumAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var alarm_time: TextView = itemView.findViewById(R.id.TodayItem_setTime)
        var take_check: TextView = itemView.findViewById(R.id.TodayItem_takeCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_today_addtakenum, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val time_list = items[position].set_date.split(" ")[1].split(":")
        var am_pm = ""
        var hour = time_list[0].toInt()
        var min = time_list[1].toInt()

        if(hour>12){
            am_pm = "오후"
            hour -= 12
        } else {
            am_pm = "오전"
        }


        holder.alarm_time.text = "${am_pm} ${hour}:${"%02d".format(min)}"
    }

    fun addItem(item: TodayTimeInfo){
        items.add(item)
    }
    fun notifyAdapter(){
        notifyDataSetChanged()
    }
    fun getItem(position: Int): TodayTimeInfo {
        return items[position]
    }
    fun deleteItem(position: Int){
        items.removeAt(position)
    }
}