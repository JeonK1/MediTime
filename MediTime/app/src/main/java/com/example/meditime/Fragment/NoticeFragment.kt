package com.example.meditime_local.Fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.AddMedicineDateActivity
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.NoticeInfo
import com.example.meditime.R
import kotlinx.android.synthetic.main.fragment_notice.*
import kotlin.properties.Delegates

/*********************************
 * 화면 #3 알림
 * 약품 알림 추가하고 확인하기
 *********************************/

class NoticeFragment : Fragment() {

    //알람 Fragment RecyclerView를 위한 변수들
    lateinit var recyclerView: RecyclerView

    companion object {
        var item = ArrayList<NoticeInfo>()
    }

    //알람 데이터들을 위한 변수들
    lateinit var medi_name: String
    var medi_no by Delegates.notNull<Int>()
    var time_no by Delegates.notNull<Int>()
    var set_cycle by Delegates.notNull<Int>()
    var set_date by Delegates.notNull<String>()
    var set_amount by Delegates.notNull<Int>()
    var re_type by Delegates.notNull<Int>()
    var re_cycle by Delegates.notNull<Int>()
    var call_alart by Delegates.notNull<Int>()
    var normal_alart by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Fragment 화면, RecyclerView 정의
        var root = inflater.inflate(R.layout.fragment_notice, container, false)
        recyclerView = root.findViewById(R.id.notice_recyclerView)
        return root
    }


    //View가 생성되고 나서 NoticeFragment를 위한 준비 부분
    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들어 화면에 뿌려주기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListenerInit()
        updateNoticeItems()
        recyclerViewInit()

    }

    fun recyclerViewInit(){
        val mediAdapter = NoticeAdapter(item)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mediAdapter
    }

    //사용자가 추가한 알림들 update
    fun updateNoticeItems() {

        //데이터 베이스 사용
        val dbHelper = DBHelper(getContext(), "MediDB.db", null, 1)
        val database = dbHelper.writableDatabase
        var dbCreater: DBCreater
        dbCreater = DBCreater(dbHelper, database)

        //사용자 알림 내용들을 데이터베이스에서 가져와 item에 추가
        NoticeFragment.item.clear()
        val cursor: Cursor = dbCreater.JoinAndSelectAll()

        if (cursor.getCount() == 0) {
            Log.d("cursor1_t1: ", "데이터 없음")
        }

        while (cursor.moveToNext()) {
            medi_name = cursor.getString(cursor.getColumnIndex("medi_name"))
            medi_no = cursor.getInt(cursor.getColumnIndex("medi_no"))
            time_no = cursor.getInt(cursor.getColumnIndex("time_no"))
            set_cycle = cursor.getInt(cursor.getColumnIndex("set_cycle"))
            set_date = cursor.getString(cursor.getColumnIndex("set_date"))
            set_amount = cursor.getInt(cursor.getColumnIndex("set_amount"))
            re_type = cursor.getInt(cursor.getColumnIndex("re_type"))
            re_cycle = cursor.getInt(cursor.getColumnIndex("re_cycle"))
            call_alart = cursor.getInt(cursor.getColumnIndex("call_alart"))
            normal_alart = cursor.getInt(cursor.getColumnIndex("normal_alart"))

            NoticeFragment.item.add(
                NoticeInfo(
                    medi_name, medi_no, time_no, set_cycle, set_date,
                    set_amount, re_type, re_cycle, call_alart, normal_alart
                )
            )
        }

        cursor.close()
    }

    private fun clickListenerInit() {

        add_medi_btn.setOnClickListener {
            // AddMedicineDateActivity 실행
            val intent = Intent(context, AddMedicineDateActivity::class.java)
            startActivity(intent)
        }
    }

    // RecyclerView를 위한 Adpater
    class NoticeAdapter(val items: ArrayList<NoticeInfo>) :
        RecyclerView.Adapter<NoticeAdapter.MyViewHolder>() {

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.item_title)
            var context: TextView = itemView.findViewById(R.id.item_context)
            var icon: ImageView = itemView.findViewById(R.id.item_icon)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): NoticeAdapter.MyViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notice_alarm, parent, false)
            return MyViewHolder(v)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: NoticeAdapter.MyViewHolder, position: Int) {
            var is_am = ""
            var hour = items[position].set_date
            /**
            if (items[position].alarm_hour > 12) {
            is_am = "오후"
            hour -= 12
            } else {
            is_am = "오전"
            }
             **/
            holder.title.text =
                "" + items[position].set_date + " " + items[position].medi_name + " " + items[position].medi_no + " " + items[position].time_no
            holder.context.text = items[position].medi_name
        }

        fun getItems(position: Int): NoticeInfo {
            return items[position]
        }

        fun deleteItem(position: Int) {
            items.removeAt(position)
        }

        fun addItem(NoticeInfo: NoticeInfo) {
            items.add(NoticeInfo)
        }
    }
}

/*** RecyclerView 뿌려주기 끝나면 참고하면서 기능 추가하려고 러프하게 작성한 코드 ***/
//class NoticeFragment : Fragment() {
//
//    lateinit var recyclerView: RecyclerView
//    lateinit var testAdapter: TestAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        var root = inflater.inflate(R.layout.fragment_notice, container, false)
//        recyclerView = root.findViewById(R.id.notice_recyclerView)
//        return root
//    }
//
//    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        testAdapter = TestAdapter(get_dummy_data())
//        recyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = testAdapter
//        clickListenerInit()
//    }
//
//    private fun clickListenerInit() {
//        add_medi_btn.setOnClickListener {
//            // AddMedicineDateActivity 실행
//            val intent = Intent(context, AddMedicineDateActivity::class.java)
//            startActivity(intent)
//        }
//        testAdapter.itemClickListener = object : TestAdapter.OnItemClickListener {
//            override fun OnItemClick(holder: TestAdapter.MyViewHolder, view: View, position: Int) {
//                // 아이템 자체를 클릭했을 때
//                // todo : alarm_cnt, set_cycle, medi_alarm_list를 실제 데이터에 적용
//                val alarm_cnt = 2
//                val set_cycle = 0
//                val medi_alarm_list =
//                    arrayListOf(AlarmInfo(10, 0, 0.5, "정"), AlarmInfo(23, 0, 1.0, "정"))
//
//                // dialogview 만들기
//                val mDialogView =
//                    LayoutInflater.from(context).inflate(R.layout.alarm_set_dialog, null)
//                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
//                val mAlertDialog = mBuilder.show()
//                mAlertDialog.window!!.setGravity(Gravity.CENTER)
//                mDialogView.tv_alarmsetdlg_name.text = holder.test_name.text
//                mDialogView.tv_alarmsetdlg_cycle_cnt.text = "1일 ${alarm_cnt}회 복용"
//                if (set_cycle == 0)
//                    mDialogView.tv_alarmsetdlg_cycle_type.text = "매일"
//                else
//                    mDialogView.tv_alarmsetdlg_cycle_type.text =
//                        "개발 중.. " // todo : set_cycle이 1일 때 작업 하기
//                // todo : image btn 작업하기
//                mDialogView.ib_alarmsetdlg_call.setOnClickListener {
//                    // todo : call 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "call 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.ib_alarmsetdlg_bell.setOnClickListener {
//                    // todo : bell 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "bell 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.btn_alarmsetdlg_save.setOnClickListener {
//                    // todo : 완료 버튼 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "완료 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.tv_alarmsetdlg_delete.setOnClickListener {
//                    // todo : 삭제 기능
//                    Toast.makeText(context, "삭제 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.tv_alarmsetdlg_modify.setOnClickListener {
//                    // todo : 수정 기능
//                    Toast.makeText(context, "수정 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun OnCallBtnClick(
//                holder: TestAdapter.MyViewHolder,
//                view: View,
//                position: Int
//            ) {
//                // 전화기 버튼 눌렀을 때
//                if (holder.call_flag) {
//                    holder.test_call.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//                } else {
//                    holder.test_call.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//                }
//                holder.call_flag = !holder.call_flag
//            }
//
//            override fun OnBellBtnClick(
//                holder: TestAdapter.MyViewHolder,
//                view: View,
//                position: Int
//            ) {
//                // 알람 버튼 눌렀을 때
//                if (holder.bell_flag) {
//                    holder.test_bell.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//                } else {
//                    holder.test_bell.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//                }
//                holder.bell_flag = !holder.bell_flag
//            }
//        }
//    }
//
//    private fun get_dummy_data(): java.util.ArrayList<TestInfo> {
//        val outList = java.util.ArrayList<TestInfo>()
//        outList.add(TestInfo("타이레놀", 10, 0, 0, null, null, 1, 0))
//        outList.add(TestInfo("비타민C", 13, 0, 1, 1, 5, 0, 0))
//        outList.add(TestInfo("영양제", 10, 10, 1, 0, 42, 0, 1))
//        return outList
//    }
//}