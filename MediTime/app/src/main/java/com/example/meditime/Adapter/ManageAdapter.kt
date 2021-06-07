package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.ManageInfo
import com.example.meditime.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManageAdapter(val items: ArrayList<ArrayList<ManageInfo>>) :
    RecyclerView.Adapter<ManageAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: ManageAdapter.MyViewHolder, view: View, position: Int)
    }

    var itemClickListener: ManageAdapter.OnItemClickListener?=null // 버튼클릭 listener

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medi_name: TextView = itemView.findViewById(R.id.manageItem_mediName)
        var icon_list = arrayListOf<ImageView>(
            itemView.findViewById(R.id.mon_icon),
            itemView.findViewById(R.id.tues_icon),
            itemView.findViewById(R.id.wed_icon),
            itemView.findViewById(R.id.thur_icon),
            itemView.findViewById(R.id.fri_icon),
            itemView.findViewById(R.id.sat_icon),
            itemView.findViewById(R.id.sun_icon)
        )
        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_alarm, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ManageAdapter.MyViewHolder, position: Int) {
        val simpleDateFormat2 = SimpleDateFormat("yyyy-MM-dd (E)")
        val today_date = Calendar.getInstance()
        var name = items[position][0].medi_name
        holder.medi_name.text = name
        for (i in 0..6){
            if(items[position][i].time_list.size==0){
                // 일정 없음
                holder.icon_list[i].setImageResource(R.drawable.ic_x)
                holder.icon_list[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorGrayDark))
            } else if(items[position][i].status==1){
                // 복용 완료
                holder.icon_list[i].setImageResource(R.drawable.ic_check)
                holder.icon_list[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorGreen))
            } else if(items[position][i].medi_date < simpleDateFormat2.format(today_date.time)){
                // 복용 시간 초과
                holder.icon_list[i].setImageResource(R.drawable.ic_x)
                holder.icon_list[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorRed))
            } else {
                // 복용 예정
                holder.icon_list[i].setImageResource(R.drawable.ic_question)
                holder.icon_list[i].setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorBlack))
            }
        }
    }
}