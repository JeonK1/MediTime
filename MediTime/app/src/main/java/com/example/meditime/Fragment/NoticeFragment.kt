package com.example.meditime_local.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.AddMedicineDateActivity
import com.example.meditime.R
import kotlinx.android.synthetic.main.fragment_notice.*

/*********************************
 * 화면 #3 알림
 * 약품 알림 추가하고 확인하기
 *********************************/

class NoticeFragment : Fragment() {

    lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_notice, container, false)
        recyclerView = root.findViewById(R.id.notice_recyclerView)
        return root
    }


    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediList = getDummyMediItems()
        val mediAdapter = MediAdapter(mediList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mediAdapter

        clickListenerInit()
    }

    private fun getDummyMediItems(): ArrayList<MediInfo> {
        val outList = ArrayList<MediInfo>()
        outList.add(MediInfo(
            alarm_name = "타이레놀",
            alarm_hour = 8,
            alarm_min = 0,
            alarm_type = 0,
            is_call = true
        ))
        outList.add(MediInfo(
            alarm_name = "혈압약",
            alarm_hour = 8,
            alarm_min = 0,
            alarm_type = 0,
            is_call = true
        ))
        return outList
    }

    private fun clickListenerInit() {
        add_medi_btn.setOnClickListener {
            // AddMedicineDateActivity 실행
            val intent = Intent(context, AddMedicineDateActivity::class.java)
            startActivity(intent)
        }
    }
/**
    override fun createNewActivity(context: Context) {
        val intent = Intent(context, NewActivity::class.java)
        startActivity(intent)
    }
**/
class MediAdapter(val items: ArrayList<MediInfo>) :
    RecyclerView.Adapter<MediAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.item_title)
        var context: TextView = itemView.findViewById(R.id.item_context)
        var icon: ImageView = itemView.findViewById(R.id.item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_notice_alarm, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MediAdapter.MyViewHolder, position: Int) {
        var is_am = ""
        var hour = items[position].alarm_hour
        var min = items[position].alarm_min

        if (items[position].alarm_hour > 12) {
            is_am = "오후"
            hour -= 12
        } else {
            is_am = "오전"
        }

        holder.title.text = items[position].alarm_name
        holder.context.text = "${is_am}${hour}:${"%02d".format(min)}"
    }

    fun getItems(position: Int): MediInfo {
        return items[position]
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
    }

    fun addItem(mediInfo: MediInfo) {
        items.add(mediInfo)
    }
}

    class MediInfo(
        val alarm_name: String,
        val alarm_type: Int,
        val alarm_hour: Int,
        val alarm_min: Int,
        val is_call: Boolean
    ) {
    }


}