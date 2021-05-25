package com.example.meditime.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Model.TodayInfo
import com.example.meditime.R
import kotlinx.android.synthetic.main.alarm_set_dialog.view.*
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
        var alarmTime: TextView = itemView.findViewById(R.id.TodayItem_setTime)
        var takeCheck: TextView = itemView.findViewById(R.id.TodayItem_takeCheck)
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
        var name = items[position].medi_name
        var timeInfoList = items[position].time_list

        // 여러개의 복용시간 추가를 위한 RecyclerView

        /**
        val addTakeNumAdapter = TodayTakeNumAdapter(items[position].time_list)

        mDialogView.rv_alarmsetdlg_cycle.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mDialogView.rv_alarmsetdlg_cycle.adapter = addTakeNumAdapter
 **/
//        for (i in 0 until timeInfoList.size){

            val setdateTime = timeInfoList[0].set_date.split(" ")[1].split(":")
            var am_pm = ""
            var hour = setdateTime[0].toInt()
            var min = setdateTime[1].toInt()

            if(hour>12){
                am_pm = "오후"
                hour -= 12
            } else {
                am_pm = "오전"
            }

            if(timeInfoList[0].set_check == 1){
                holder.takeCheck.text = "복용완료"
            }
            else if(timeInfoList[0].set_check == -1){

                holder.takeCheck.text = "복용안함"
            }

            holder.alarmTime.text = "${am_pm} ${hour}:${"%02d".format(min)}"



        holder.medi_name.text = name


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