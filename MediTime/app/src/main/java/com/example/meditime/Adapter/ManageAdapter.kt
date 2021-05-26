package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.ManageInfo
import com.example.meditime.Model.TodayInfo
import com.example.meditime.R

class ManageAdapter(val items: ArrayList<ManageInfo>) :
    RecyclerView.Adapter<ManageAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: ManageAdapter.MyViewHolder, view: View, position: Int)
        fun OnCallBtnClick(holder: ManageAdapter.MyViewHolder, view: View, position: Int)
        fun OnBellBtnClick(holder: ManageAdapter.MyViewHolder, view: View, position: Int)
    }

    var itemClickListener: ManageAdapter.OnItemClickListener?=null // 버튼클릭 listener

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var medi_name: TextView = itemView.findViewById(R.id.manageItem_mediName)
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
    ): ManageAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_alarm, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ManageAdapter.MyViewHolder, position: Int) {
        var name = items[position].medi_name
        holder.medi_name.text = name

    }

    fun getItems(position: Int): ManageInfo {
        return items[position]
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
    }

    fun addItem(manageInfo: ManageInfo) {
        items.add(manageInfo)
    }
}