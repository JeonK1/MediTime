package com.example.meditime.activity

import android.app.Activity
import android.app.AlarmManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.*
import com.example.meditime.adapter.AlarmAdapter
import com.example.meditime.database.DBCreater
import com.example.meditime.database.DBHelper
import com.example.meditime.model.NoticeAlarmInfo
import com.example.meditime.model.NoticeInfo
import com.example.meditime.util.AlarmCallManager
import com.example.meditime.util.DowConverterFactory
import kotlinx.android.synthetic.main.activity_add_medicine_time.*
import java.util.*
import kotlin.collections.ArrayList

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 목록이 있는 화면
 *********************************/

class AddMedicineTimeActivity : AppCompatActivity() {

    companion object {
        // 상수 선언
        private const val ADD_MEDICINE_TIME_SET = 201
    }

    private lateinit var alarmAdapter: AlarmAdapter
    var alarmList = ArrayList<NoticeAlarmInfo>()

    // AddMedicineDateActivity 에서 받아오는 내용
    private var cur_noticeInfo = NoticeInfo()
    var type = "" // modify(수정) | add(추가)

    //데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    //AlarmManager
    private lateinit var alarmCallManager: AlarmCallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time)
        globalInit()
        getIntentData()
        recyclerViewInit()
        alarmCountCheck()
        listenerInit()
    }

    private fun globalInit() {
        // AlarmCallManager
        alarmCallManager = AlarmCallManager(this)

        // Database
        dbHelper = DBHelper(this, "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    private fun alarmCountCheck() {
        if (alarmAdapter.items.size == 0) {
            // OK 버튼, 알람의 개수가 0개이면 비활성화하기
            btn_addmeditime_ok.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorGrayDark2)
            btn_addmeditime_ok.isEnabled = false
            // 알람의 개수가 0개이면, 가운데에 알람을 추가해주세요 문구 보여주기
            ll_addmeditime_no_element_view.visibility = View.VISIBLE
        } else {
            // OK 버튼, 알람의 개수가 0개가 아니면 활성화하기
            btn_addmeditime_ok.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorBlue)
            btn_addmeditime_ok.isEnabled = true
            // 알람의 개수가 0개가 아니면, 가운데에 알람을 추가해주세요 문구 제거`
            ll_addmeditime_no_element_view.visibility = View.GONE
        }
    }

    private fun getIntentData() {
        // AddMedicineDataActivity로부터 받은 데이터 적용하기
        type = intent.getStringExtra("type").toString()
        cur_noticeInfo = intent.getSerializableExtra("noticeInfo2") as NoticeInfo
        tv_addmeditime_name.text = cur_noticeInfo.medi_name
        if (type == "modify") {
            // 수정 으로 넘어온 것일 때
            // 알람 정보 넘겨주기
            alarmList = cur_noticeInfo.time_list
        }
    }

    private fun recyclerViewInit() {
        // recyclerView 초기설정
        alarmAdapter = AlarmAdapter(alarmList)
        rv_addmeditime_container.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_addmeditime_container.adapter = alarmAdapter
    }

    private fun listenerInit() {
        ib_addmeditime_backbtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
        }
        btn_addmeditime_ok.setOnClickListener {
            // 완료 버튼 클릭 시
            if (type == "add") {
                // 데이터베이스에 추가
                val start_date_split = cur_noticeInfo.start_date.split("-")

                // table1 insert
                if (cur_noticeInfo.set_cycle == 0) {
                    cur_noticeInfo.medi_no = dbCreater.insertColumn_table1(
                        medi_name = cur_noticeInfo.medi_name,
                        set_cycle = cur_noticeInfo.set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = "null",
                        re_cycle = "null",
                        call_alart = cur_noticeInfo.call_alart.toString(),
                        normal_alart = cur_noticeInfo.normal_alart.toString()
                    ).toInt()
                } else {
                    cur_noticeInfo.medi_no = dbCreater.insertColumn_table1(
                        medi_name = cur_noticeInfo.medi_name,
                        set_cycle = cur_noticeInfo.set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = cur_noticeInfo.re_type.toString(),
                        re_cycle = cur_noticeInfo.re_cycle.toString(),
                        call_alart = cur_noticeInfo.call_alart.toString(),
                        normal_alart = cur_noticeInfo.normal_alart.toString()
                    ).toInt()
                }

                // get cursor for medi_no
                val cursor = dbCreater.selectColumn("table1", "*", "1=1 order by medi_no desc")
                cursor.moveToFirst()

                // table2 insert
                val medi_no = cursor.getInt(cursor.getColumnIndex("medi_no"))
                for (item in alarmAdapter.items) {
                    // insert alarm info
                    item.time_no = dbCreater.insertColumn_table2(
                        medi_no = medi_no.toString(),
                        set_amount = item.set_amount.toString(),
                        set_type = item.set_type,
                        set_date = item.set_date,
                        take_date = "null",
                        set_check = "0"
                    ).toInt()

                    // insert alarm_table + set notification
                    when{
                        cur_noticeInfo.set_cycle==0 -> {
                            // 매일 반복, 반복 alarm 1개 필요
                            val alarm_no = dbCreater.insert_alarm(time_no = item.time_no)
                            dbCreater.insertRecordWeek(
                                alarm_no = alarm_no.toInt(),
                                time_no = item.time_no,
                                alarm_datetime = item.set_date,
                                set_cycle = cur_noticeInfo.set_cycle,
                                re_type = cur_noticeInfo.re_type,
                                re_cycle = cur_noticeInfo.re_cycle
                            )
                            alarmCallManager.setAlarmRepeating(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date,
                                interval_millis = AlarmManager.INTERVAL_DAY // 매일 알람 맞추기
                            )
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==0 -> {
                            // 요일 반복, 반복 alarm 요일 개수 만큼 구현하기
                            val start_date_split = item.set_date.split(" ")[0].split("-")
                            val start_time_split = item.set_date.split(" ")[1].split(":")
                            val calendar = Calendar.getInstance()
                            calendar.set(
                                start_date_split[0].toInt(), // year
                                start_date_split[1].toInt() - 1, // month
                                start_date_split[2].toInt(), // date
                                start_time_split[0].toInt(), // hour
                                start_time_split[1].toInt(), // minute
                                start_time_split[2].toInt()  // second
                            )
                            // 만약에 alarm_datetime이.. 오늘 이전이면 오늘 이후까지 이동
                            val today_calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            }
                            // 요일반복 (하루씩 늘림 + 현재 요일이 해당되어야함)
                            val dow_arrayList =
                                DowConverterFactory.convert_int_to_arrayList(cur_noticeInfo.re_cycle)
                            while (calendar < today_calendar && dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK) - 1]) {
                                calendar.add(Calendar.DATE, 1)
                            }

                            // alarm_date가 있는 주의 일요일
                            val calendar_end = calendar.clone() as Calendar
                            while (calendar_end.get(Calendar.DAY_OF_WEEK) != 1) {
                                calendar_end.add(Calendar.DATE, 1)
                            }

                            // 이번 주의 해당하는 (오늘 이후의) 요일들에 대해 반복 알람 설정
                            var last_record_no = 0
                            while (calendar_end.compareTo(calendar) != -1) {
                                if (dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK) - 1]) {
                                    val alarm_no = dbCreater.insert_alarm(time_no = item.time_no)
                                    last_record_no = dbCreater.insertRecord(
                                        alarm_no = alarm_no.toInt(),
                                        time_no = item.time_no,
                                        alarm_datetime = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)} " +
                                                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
                                    ).toInt()
                                    alarmCallManager.setAlarmRepeating(
                                        alarm_id = alarm_no.toInt(),
                                        start_date_str = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)} " +
                                                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}",
                                        interval_millis = (AlarmManager.INTERVAL_DAY * 7) // 주간 반복 알람 맞추기
                                    )
                                }
                                calendar.add(Calendar.DATE, 1)
                            }
                            dbCreater.set_record_is_last(last_record_no, 1)
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==1 -> {
                            // N 일 반복, 반복 alarm 1개 필요
                            val alarm_no = dbCreater.insert_alarm(time_no = item.time_no)
                            dbCreater.insertRecordWeek(
                                alarm_no = alarm_no.toInt(),
                                time_no = item.time_no,
                                alarm_datetime = item.set_date,
                                set_cycle = cur_noticeInfo.set_cycle,
                                re_type = cur_noticeInfo.re_type,
                                re_cycle = cur_noticeInfo.re_cycle
                            )
                            alarmCallManager.setAlarmRepeating(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date,
                                interval_millis = AlarmManager.INTERVAL_DAY * cur_noticeInfo.re_cycle // N일 반복 알람 맞추기
                            )
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==2 -> {
                            // 개월 반복, alarm 1개 필요, alarm 체크 마다 다음 알람 설정하기
                            val alarm_no = dbCreater.insert_alarm(time_no = item.time_no)
                            val record_no = dbCreater.insertRecord(
                                alarm_no = alarm_no.toInt(),
                                time_no = item.time_no,
                                alarm_datetime = item.set_date
                            ).toInt()
                            dbCreater.set_record_is_last(record_no, 1)
                            alarmCallManager.setAlarm(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date
                            )
                        }
                    }
                }
            } else if (type == "modify") {
                // 데이터베이스 수정
                // table1 modify
                if (cur_noticeInfo.set_cycle == 0) {
                    dbCreater.updateColumn(
                        "table1",
                        "medi_name=\"${cur_noticeInfo.medi_name}\", " +
                                "set_cycle=${cur_noticeInfo.set_cycle}," +
                                "start_date=\"${cur_noticeInfo.start_date}\"," +
                                "re_type=${null}," +
                                "re_cycle=${null}",
                        "medi_no=${cur_noticeInfo.medi_no}"
                    )
                } else {
                    dbCreater.updateColumn(
                        "table1",
                        "medi_name=\"${cur_noticeInfo.medi_name}\", " +
                                "set_cycle=${cur_noticeInfo.set_cycle}," +
                                "start_date=\"${cur_noticeInfo.start_date}\"," +
                                "re_type=${cur_noticeInfo.re_type}," +
                                "re_cycle=${cur_noticeInfo.re_cycle}",
                        "medi_no=${cur_noticeInfo.medi_no}"
                    )
                }

                // table2 modfiy
                // medi_no와 연결된 알람 모두 지우기
                for (cur_time_no in dbCreater.get_time_no_by_medi_no(cur_noticeInfo.medi_no)){
                    for (cur_alarm_no in dbCreater.get_alarm_no_by_time_no(cur_time_no)){
                        dbCreater.delete_alarm_by_alarm_no(cur_alarm_no)
                        dbCreater.delete_record_by_alarm_no(cur_alarm_no)
                        alarmCallManager.cancelAlarm_alarm_id(cur_alarm_no)
                    }
                }
                // delete all of alarm info
                dbCreater.delete_alarm_all_by_medi_no(cur_noticeInfo.medi_no)

                val cur_time_no = alarmAdapter.items[0].time_no
                for (item in alarmAdapter.items) {
                    // insert alarm info
                    val new_time_no = dbCreater.insertColumn_table2(
                        medi_no = cur_noticeInfo.medi_no.toString(),
                        set_amount = item.set_amount.toString(),
                        set_type = item.set_type,
                        set_date = item.set_date,
                        take_date = "null",
                        set_check = "0"
                    ).toInt()

                    // insert alarm_table + set notification
                    when{
                        cur_noticeInfo.set_cycle==0 -> {
                            // 매일 반복, 반복 alarm 1개 필요
                            val alarm_no = dbCreater.insert_alarm(time_no = new_time_no)
                            dbCreater.insertRecordWeek(
                                alarm_no = alarm_no.toInt(),
                                time_no = cur_time_no,
                                alarm_datetime = item.set_date,
                                set_cycle = cur_noticeInfo.set_cycle,
                                re_type = cur_noticeInfo.re_type,
                                re_cycle = cur_noticeInfo.re_cycle
                            )
                            alarmCallManager.setAlarmRepeating(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date,
                                interval_millis = AlarmManager.INTERVAL_DAY // 매일 알람 맞추기
                            )
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==0 -> {
                            // 요일 반복, 반복 alarm 요일 개수 만큼 구현하기
                            val start_date_split = item.set_date.split(" ")[0].split("-")
                            val start_time_split = item.set_date.split(" ")[1].split(":")
                            val calendar = Calendar.getInstance()
                            calendar.set(
                                start_date_split[0].toInt(), // year
                                start_date_split[1].toInt()-1, // month
                                start_date_split[2].toInt(), // date
                                start_time_split[0].toInt(), // hour
                                start_time_split[1].toInt(), // minute
                                start_time_split[2].toInt()  // second
                            )
                            val dow_arrayList = DowConverterFactory.convert_int_to_arrayList(cur_noticeInfo.re_cycle)
                            for (i in 0..6){
                                if(dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK)-1]){
                                    val alarm_no = dbCreater.insert_alarm(time_no = item.time_no)
                                    dbCreater.insertRecord(
                                        alarm_no = alarm_no.toInt(),
                                        time_no = item.time_no,
                                        alarm_datetime = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)} " +
                                                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
                                    )
                                    alarmCallManager.setAlarmRepeating(
                                        alarm_id = alarm_no.toInt(),
                                        start_date_str = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)} " +
                                                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}",
                                        interval_millis = (AlarmManager.INTERVAL_DAY*7) // 주간 반복 알람 맞추기
                                    )
                                }
                                calendar.add(Calendar.DATE, 1)
                            }
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==1 -> {
                            // N 일 반복, 반복 alarm 1개 필요
                            val alarm_no = dbCreater.insert_alarm(time_no = new_time_no)
                            dbCreater.insertRecordWeek(
                                alarm_no = alarm_no.toInt(),
                                time_no = item.time_no,
                                alarm_datetime = item.set_date,
                                set_cycle = cur_noticeInfo.set_cycle,
                                re_type = cur_noticeInfo.re_type,
                                re_cycle = cur_noticeInfo.re_cycle
                            )
                            alarmCallManager.setAlarmRepeating(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date,
                                interval_millis = AlarmManager.INTERVAL_DAY * cur_noticeInfo.re_cycle // N일 반복 알람 맞추기
                            )
                        }
                        cur_noticeInfo.set_cycle==1 && cur_noticeInfo.re_type==2 -> {
                            // 개월 반복, alarm 1개 필요, alarm 체크 마다 다음 알람 설정하기
                            val alarm_no = dbCreater.insert_alarm(time_no = new_time_no)
                            dbCreater.insertRecord(
                                alarm_no = alarm_no.toInt(),
                                time_no = item.time_no,
                                alarm_datetime = item.set_date
                            )
                            alarmCallManager.setAlarm(
                                alarm_id = alarm_no.toInt(),
                                start_date_str = item.set_date
                            )
                        }
                    }
                }
            }

            // 이전화면으로 되돌아가기
            setResult(Activity.RESULT_OK)
            finish()
        }
        tv_addmeditime_addbtn.setOnClickListener {
            // 추가 버튼 클릭 시
            val intent = Intent(this, AddMedicineTimeSetActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("noticeInfo2", cur_noticeInfo)
            intent.putExtras(bundle)
            startActivityForResult(intent, ADD_MEDICINE_TIME_SET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_MEDICINE_TIME_SET && resultCode == Activity.RESULT_OK) {
            // AddMedicineTimeSetActivity 에서 call back
            val newAlarmInfo = data?.getSerializableExtra("noticeInfo3") as NoticeAlarmInfo
            alarmAdapter.addItem(newAlarmInfo)
            alarmAdapter.notifyAdapter()
            alarmCountCheck()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로가기 핸드폰 버튼 클릭 시
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
    }
}
