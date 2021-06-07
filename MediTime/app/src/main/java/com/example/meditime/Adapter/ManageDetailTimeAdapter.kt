package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Model.ManageTimeInfo
import com.example.meditime.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManageDetailTimeAdapter(val items: ArrayList<ManageTimeInfo>) :
    RecyclerView.Adapter<ManageDetailTimeAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_time:TextView = itemView.findViewById(R.id.tv_alarmitem_time)
        val tv_description:TextView = itemView.findViewById(R.id.tv_alarmitem_description)
        val iv_icon:ImageView = itemView.findViewById(R.id.iv_manageInfoTime_icon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageDetailTimeAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manageinfo_detail_time, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ManageDetailTimeAdapter.MyViewHolder, position: Int) {
        val sdf = SimpleDateFormat("a hh:mm")
        val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdf3 = SimpleDateFormat("yyyy-MM-dd")
        holder.tv_time.text = sdf.format(sdf2.parse(items[position].record_date))
        if(items[position].take_date!=null){
            // 복용 완료
            holder.iv_icon.setImageResource(R.drawable.ic_check)
            holder.iv_icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorGreen))
        } else if(items[position].record_date.split(" ")[0] < sdf3.format(Calendar.getInstance().time)) {
            // 복용 시간 초과
            holder.iv_icon.setImageResource(R.drawable.ic_x)
            holder.iv_icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorRed))
        } else {
            // 복용 예정
            holder.iv_icon.setImageResource(R.drawable.ic_question)
            holder.iv_icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.colorBlack))
        }
    }
}