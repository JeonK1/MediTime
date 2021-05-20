package com.example.meditime.Activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.*
import com.example.meditime.Adapter.AlarmAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.Model.AlarmInfo
import com.example.meditime_local.Fragment.NoticeFragment
import kotlinx.android.synthetic.main.activity_add_medicine_time.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 목록이 있는 화면
 *********************************/

class AddMedicineTimeActivity : AppCompatActivity() {

    val NoticeFragment = com.example.meditime_local.Fragment.NoticeFragment()
    val ADD_MEDICINE_TIME_SET = 201
    lateinit var alarmAdapter: AlarmAdapter
    var alarmList = ArrayList<AlarmInfo>()

    // AddMedicineDateActivity 에서 받아오는 내용
    var medi_name:String? = null
    var set_cycle:Int = -1
    var start_date:String? = null
    var re_type:Int = -1
    var re_cycle:Int = -1

    var type = "" // modify(수정) | add(추가)
    var medi_no:Int = -1 // 수정 하러 넘어온 것 일 때의 medi_no 값

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
        medi_name = intent.getStringExtra("medi_name")
        set_cycle = intent.getIntExtra("set_cycle", -1)
        start_date = intent.getStringExtra("start_date")
        re_type = intent.getIntExtra("re_type", -1)
        re_cycle = intent.getIntExtra("re_cycle", -1)
        type = intent.getStringExtra("type").toString()
        if(medi_name!=null && medi_name!="")
            tv_addmeditime_name.text = medi_name
        if(type=="modify") {
            // 수정 으로 넘어온 것일 때
            // 알람 정보 넘겨주기
            medi_no = intent.getIntExtra("medi_no", -1)
            alarmList = dbCreater.get_alarm_by_medi_no(medi_no)
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
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btn_addmeditime_ok.setOnClickListener {
            // 완료 버튼 클릭 시
            if(type=="add"){
                // 데이터베이스에 추가
                val start_date_split = start_date!!.split(". ")
                // table1 insert
                if(set_cycle==0){
                    dbCreater.insertColumn_table1(
                        medi_name = medi_name!!,
                        set_cycle = set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = "null",
                        re_cycle = "null",
                        call_alart = "0",
                        normal_alart = "1"
                    )
                } else {
                    dbCreater.insertColumn_table1(
                        medi_name = medi_name!!,
                        set_cycle = set_cycle.toString(),
                        start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}",
                        re_type = re_type.toString(),
                        re_cycle = re_cycle.toString(),
                        call_alart = "0",
                        normal_alart = "1"
                    )
                }

                // get cursor for medi_no
                val cursor = dbCreater.selectColumn("table1", "*", "1=1 order by medi_no desc")!!
                cursor.moveToFirst()

                // table2 insert
                val medi_no = cursor.getInt(cursor.getColumnIndex("medi_no"))
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                for (item in alarmAdapter.items){
                    val curDate = Date()
                    curDate.hours = item.alarm_hour
                    curDate.minutes = item.alarm_min
                    curDate.seconds = 0
                    dbCreater.insertColumn_table2(
                        medi_no = medi_no.toString(),
                        set_amount = item.medicine_count.toString(),
                        set_type = item.medicine_type,
                        set_date = "${simpleDateFormat.format(curDate)}",
                        take_date = "null",
                        set_check = "0"
                    )
                }
            } else if(type=="modify"){
                // 데이터베이스 수정
                val start_date_split = start_date!!.split(". ")
                // table1 modify
                if(set_cycle==0){
                    dbCreater.updateColumn("table1",
                        "medi_name=\"${medi_name}\", " +
                                "set_cycle=${set_cycle}," +
                                "start_date=\"${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}\"," +
                                "re_type=${null}," +
                                "re_cycle=${null}",
                        "medi_no=${medi_no}"
                    )
                } else {
                    dbCreater.updateColumn("table1",
                        "medi_name=\"${medi_name}\", " +
                                "set_cycle=${set_cycle}," +
                                "start_date=\"${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2]}\"," +
                                "re_type=${re_type}," +
                                "re_cycle=${re_cycle}",
                        "medi_no=${medi_no}"
                    )
                }
                // table2 modfiy
                dbCreater.delete_alarm_all_by_medi_no(medi_no)
                for (item in alarmAdapter.items){
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val curDate = Date()
                    curDate.hours = item.alarm_hour
                    curDate.minutes = item.alarm_min
                    curDate.seconds = 0
                    dbCreater.insertColumn_table2(
                        medi_no = medi_no.toString(),
                        set_amount = item.medicine_count.toString(),
                        set_type = item.medicine_type,
                        set_date = "${simpleDateFormat.format(curDate)}",
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
            val newAlarmInfo = data?.getSerializableExtra("alarmInfo") as AlarmInfo
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
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }



}
