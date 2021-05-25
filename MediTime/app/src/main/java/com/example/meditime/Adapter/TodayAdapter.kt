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
        var medi_name: TextView = itemView.findViewById(R.id.TodayItem_mediName)
        var AlarmTime: TextView = itemView.findViewById(R.id.TodayItem_setTime)

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