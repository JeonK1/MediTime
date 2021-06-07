package com.example.meditime_local.Fragment

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.MainActivity
import com.example.meditime.Adapter.TodayAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.R
import kotlinx.android.synthetic.main.alarm_check_dialog.view.*
import kotlinx.android.synthetic.main.fragment_today.*
import java.text.SimpleDateFormat
import java.util.*

/*********************************
 * 화면 #1 오늘
 * 해당일자 약품 알림 확인하기
 *********************************/

class TodayFragment : Fragment() {

    val TIME_LIMIT_INTERVAL_FIVE_MIN = 300000L // 5분
    val TIME_LIMIT_INTERVAL_ONE_MIN = 60000L // 1분

    lateinit var recyclerView: RecyclerView
    lateinit var TodayAlarmAdapter: TodayAdapter

    // 데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_today, container, false)
        recyclerView = root.findViewById(R.id.today_recyclerView)
        return root
    }

    //DataBase에서 해당일자 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelperInit()
        fragment_init()
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(getContext(), "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    fun fragment_init() {
        recyclerViewInit()
        clickListenerInit()
    }

    fun recyclerViewInit() {
        TodayAlarmAdapter = TodayAdapter(dbCreater.get_today_info_record())
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = TodayAlarmAdapter
    }

    private fun clickListenerInit() {
        TodayAlarmAdapter.itemClickListener = object : TodayAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: TodayAdapter.MyViewHolder,
                view: View,
                position: Int
            ) {
                val medi_name = TodayAlarmAdapter.items.get(position).medi_name
                val set_date = TodayAlarmAdapter.items.get(position).record_date
                val take_date = TodayAlarmAdapter.items.get(position).take_date
                val record_no = TodayAlarmAdapter.items.get(position).record_no

                // dialogview 만들기
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.alarm_check_dialog, null)
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setGravity(Gravity.CENTER)

                // dialogView 내용
                val setdateTime = set_date.split(" ")[1].split(":")
                var alarm_am_pm = ""
                var alarm_hour = setdateTime[0].toInt()
                var alarm_min = setdateTime[1].toInt()

                if (alarm_hour > 12) {
                    alarm_am_pm = "오후"
                    alarm_hour -= 12
                } else {
                    alarm_am_pm = "오전"
                }

                mDialogView.alarmCheckDialog_mediName.text = medi_name

                if(take_date==null){
                    mDialogView.alarmCheckDialog_btnlayout_notake.visibility = View.VISIBLE
                    mDialogView.alarmCheckDialog_btnlayout_take.visibility = View.GONE
                    mDialogView.alarmCheckDialog_msg.text = "복용을 완료하셨습니까?"

                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val alarm_time = Calendar.getInstance()
                    alarm_time.time = simpleDateFormat.parse(set_date)
                    if(Calendar.getInstance().time.time - alarm_time.time.time < TIME_LIMIT_INTERVAL_FIVE_MIN){
                        mDialogView.alarmCheckDialog_setTime.text =
                            "${alarm_am_pm} ${alarm_hour}:${"%02d".format(alarm_min)} 복용예정"
                        mDialogView.alarmCheckDialog_setTime.setTextColor(resources.getColor(R.color.colorBlack))
                    } else {
                        mDialogView.alarmCheckDialog_setTime.text =
                            "${alarm_am_pm} ${alarm_hour}:${"%02d".format(alarm_min)} 복용시간 초과"
                        mDialogView.alarmCheckDialog_setTime.setTextColor(resources.getColor(R.color.colorRed))
                    }
                } else {
                    mDialogView.alarmCheckDialog_btnlayout_notake.visibility = View.GONE
                    mDialogView.alarmCheckDialog_btnlayout_take.visibility = View.VISIBLE
                    mDialogView.alarmCheckDialog_msg.text = "복용을 완료하였습니다."
                    mDialogView.alarmCheckDialog_setTime.text =
                        "${alarm_am_pm} ${alarm_hour}:${"%02d".format(alarm_min)} 복용완료"
                    mDialogView.alarmCheckDialog_setTime.setTextColor(resources.getColor(R.color.colorGreen))
                }

                // db 적용
                // 복용 완료 버튼
                mDialogView.alarmCheckDialog_yesTake.setOnClickListener {
                    dbCreater.set_record_check(record_no)
                    mAlertDialog.cancel()
                    fragment_init() // 다시 초기화
                }

                // 복용 건너띔 버튼
                mDialogView.alarmCheckDialog_noTake.setOnClickListener {
                    mAlertDialog.cancel()
                }

                // 확인 버튼
                mDialogView.alarmCheckDialog_ok.setOnClickListener {
                    mAlertDialog.cancel()
                }
            }
        }
    }
}