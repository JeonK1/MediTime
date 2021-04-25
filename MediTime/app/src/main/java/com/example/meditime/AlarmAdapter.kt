package com.example.meditime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmAdapter(val items:ArrayList<AlarmInfo>): RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var alarm_time: TextView = itemView.findViewById(R.id.tv_alarmitem_time)
        var alarm_count: TextView = itemView.findViewById(R.id.tv_alarmitem_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var am_pm = ""
        var hour = items[position].alarm_hour
        var min = items[position].alarm_min
        var type = items[position].medicin_type

        if(items[position].alarm_hour>12){
            am_pm = "오후"
            hour -= 12
        } else {
            am_pm = "오전"
        }

        holder.alarm_time.text = "${am_pm} ${hour}:${"%02d".format(min)}"
        holder.alarm_count.text = "1일 ${items[position].medicine_count}${type} 복용"
    }

    fun addItem(item: AlarmInfo){
        items.add(item)
    }
    fun notifyAdapter(){
        notifyDataSetChanged()
    }
    fun getItem(position: Int):AlarmInfo{
        return items[position]
    }
    fun deleteItem(position: Int){
        items.removeAt(position)
    }
}