package com.example.meditime.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.*
import com.example.meditime.Adapter.AlarmAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.Model.NoticeInfo
import com.example.meditime.Model.NoticeAlarmInfo
import kotlinx.android.synthetic.main.activity_add_medicine_time.*
import kotlin.collections.ArrayList

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 목록이 있는 화면
 *********************************/

class AddMedicineTimeActivity : AppCompatActivity() {

    val NoticeFragment = com.example.meditime_local.Fragment.NoticeFragment()
    val ADD_MEDICINE_TIME_SET = 201
    lateinit var alarmAdapter: AlarmAdapter
    var alarmList = ArrayList<NoticeAlarmInfo>()

    // AddMedicineDateActivity 에서 받아오는 내용
    var cur_noticeInfo = NoticeInfo()
    var type = "" // modify(수정) | add(추가)

    //데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time)
        dbHelperInit()
        getIntentData()
        recyclerViewInit()
        okButtonInit()
        listenerInit()
    }

    private fun okButtonInit() {
        // OK 버튼, 알람의 개수가 0개이면 비활성화하기
        if(alarmAdapter.items.size==0) {
            btn_addmeditime_ok.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorGrayDark2)
            btn_addmeditime_ok.isEnabled = false
        } else {
            btn_addmeditime_ok.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorBlue)
            btn_addmeditime_ok.isEnabled = true
        }
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(this, "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    private fun getIntentData() {
        // AddMedicineDataActivity로부터 받은 데이터 적용하기
        type = intent.getStringExtra("type").toString()
        cur_noticeInfo = intent.getSerializableExtra("noticeInfo2") as NoticeInfo
        tv_addmeditime_name.text = cur_noticeInfo.medi_name
        if(type=="modify") {
            // 수정 으로 넘어온 것일 때
            // 알람 정보 넘겨주기
             alarmList = cur_noticeInfo.time_list
        }
    }

    private fun recyclerViewInit() {
        // recyclerView 초기설정
        alarmAdapter = AlarmAdapter(alarmList)
        rv_addmeditime_container.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
            if(type=="add"){
                // 데이터베이스에 추가
                val start_date_split = cur_noticeInfo.start_date.split("-")
                // table1 insert
                if(cur_noticeInfo.set_cycle==0){
                    dbCreater.insertColumn_table1(
                        medi_name = cur_noticeInfo.medi_name,
                        set_cycle = cur_noticeInfo.set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = "null",
                        re_cycle = "null",
                        call_alart = cur_noticeInfo.call_alart.toString(),
                        normal_alart = cur_noticeInfo.normal_alart.toString()
                    )
                } else {
                    dbCreater.insertColumn_table1(
                        medi_name = cur_noticeInfo.medi_name,
                        set_cycle = cur_noticeInfo.set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = cur_noticeInfo.re_type.toString(),
                        re_cycle = cur_noticeInfo.re_cycle.toString(),
                        call_alart = cur_noticeInfo.call_alart.toString(),
                        normal_alart = cur_noticeInfo.normal_alart.toString()
                    )
                }

                // get cursor for medi_no
                val cursor = dbCreater.selectColumn("table1", "*", "1=1 order by medi_no desc")!!
                cursor.moveToFirst()

                // table2 insert
                val medi_no = cursor.getInt(cursor.getColumnIndex("medi_no"))
                for (item in alarmAdapter.items){
                    dbCreater.insertColumn_table2(
                        medi_no = medi_no.toString(),
                        set_amount = item.set_amount.toString(),
                        set_type = item.set_type,
                        set_date = item.set_date,
                        take_date = "null",
                        set_check = "0"
                    )
                }
                // todo : bundle 로 보내는 방법 고민하기

            } else if(type=="modify"){
                // 데이터베이스 수정
                // table1 modify
                if(cur_noticeInfo.set_cycle==0){
                    dbCreater.updateColumn("table1",
                        "medi_name=\"${cur_noticeInfo.medi_name}\", " +
                                "set_cycle=${cur_noticeInfo.set_cycle}," +
                                "start_date=\"${cur_noticeInfo.start_date}\"," +
                                "re_type=${null}," +
                                "re_cycle=${null}",
                        "medi_no=${cur_noticeInfo.medi_no}"
                    )
                } else {
                    dbCreater.updateColumn("table1",
                        "medi_name=\"${cur_noticeInfo.medi_name}\", " +
                                "set_cycle=${cur_noticeInfo.set_cycle}," +
                                "start_date=\"${cur_noticeInfo.start_date}\"," +
                                "re_type=${cur_noticeInfo.re_type}," +
                                "re_cycle=${cur_noticeInfo.re_cycle}",
                        "medi_no=${cur_noticeInfo.medi_no}"
                    )
                }
                // table2 modfiy
                dbCreater.delete_alarm_all_by_medi_no(cur_noticeInfo.medi_no)
                for (item in alarmAdapter.items){
                    dbCreater.insertColumn_table2(
                        medi_no = cur_noticeInfo.medi_no.toString(),
                        set_amount = item.set_amount.toString(),
                        set_type = item.set_type,
                        set_date = item.set_date,
                        take_date = "null",
                        set_check = "0"
                    )
                }
            }

            // 이전화면으로 되돌아가기
            setResult(Activity.RESULT_OK)
            finish()
        }
        tv_addmeditime_addbtn.setOnClickListener {
            // 추가 버튼 클릭 시
            val intent = Intent(this, AddMedicineTimeSetActivity::class.java)
            startActivityForResult(intent, ADD_MEDICINE_TIME_SET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_MEDICINE_TIME_SET && resultCode == Activity.RESULT_OK){
            // AddMedicineTimeSetActivity 에서 call back
            val newAlarmInfo = data?.getSerializableExtra("noticeInfo3") as NoticeAlarmInfo
            if(newAlarmInfo!=null){
                alarmAdapter.addItem(newAlarmInfo)
                alarmAdapter.notifyAdapter()
                okButtonInit()
            }
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
