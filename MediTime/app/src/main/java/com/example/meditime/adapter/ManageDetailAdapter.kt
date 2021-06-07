package com.example.meditime.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.model.ManageInfo
import com.example.meditime.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManageDetailAdapter(val items: ArrayList<ManageInfo>) :
    RecyclerView.Adapter<ManageDetailAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wrapper:LinearLayout = itemView.findViewById(R.id.ll_manageInfoDetailItem_wrapper)
        val medi_date:TextView = itemView.findViewById(R.id.tv_manageInfoDetailItem_date)
        val rv_time:RecyclerView = itemView.findViewById(R.id.rv_manageInfoDetailItem_timeList)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManageDetailAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manageinfo_detail, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ManageDetailAdapter.MyViewHolder, position: Int) {
        if(items[position].time_list.size==0){
            // time_list가 비어있으면 보여주지 않음
            holder.wrapper.visibility = View.GONE
            holder.medi_date.visibility = View.GONE
            return
        } else {
            // time_list recyclerView 적용
            holder.wrapper.visibility = View.VISIBLE
            val sdf = SimpleDateFormat("yyyy-MM-dd (E)")
            val sdf2 = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREA)
            holder.medi_date.text = sdf2.format(sdf.parse(items[position].medi_date))
            holder.rv_time.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
            holder.rv_time.adapter = ManageDetailTimeAdapter(items[position].time_list)
        }

    }
}