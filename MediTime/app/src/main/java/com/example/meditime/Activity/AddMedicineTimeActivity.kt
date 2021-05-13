package com.example.meditime.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.*
import com.example.meditime.Adapter.AlarmAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.Model.AlarmInfo
import kotlinx.android.synthetic.main.activity_add_medicine_time.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 목록이 있는 화면
 *********************************/

class AddMedicineTimeActivity : AppCompatActivity() {

    val ADD_MEDICINE_TIME_SET = 201
    lateinit var alarmAdapter: AlarmAdapter

    // AddMedicineDateActivity 에서 받아오는 내용
    var medi_name:String? = null
    var set_cycle:Int = -1
    var start_date:String? = null
    var re_type:Int = -1
    var re_cycle:Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time)
        getIntentData()
        recyclerViewInit()
        listenerInit()
    }

    private fun getIntentData() {
        // AddMedicineDataActivity로부터 받은 데이터 적용하기
        medi_name = intent.getStringExtra("medi_name")
        set_cycle = intent.getIntExtra("set_cycle", -1)
        start_date = intent.getStringExtra("start_date")
        re_type = intent.getIntExtra("re_type", -1)
        re_cycle = intent.getIntExtra("re_cycle", -1)

        if(medi_name!=null && medi_name!="")
            tv_addmeditime_name.text = medi_name
    }

    private fun recyclerViewInit() {
        // recyclerView 초기설정
        val alarmList = ArrayList<AlarmInfo>()
        alarmAdapter = AlarmAdapter(alarmList)
        rv_addmeditime_container.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_addmeditime_container.adapter = alarmAdapter
    }

    private fun listenerInit() {
        ib_addmeditime_backbtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시
            finish()
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
        }
        btn_addmeditime_ok.setOnClickListener {
            // 완료 버튼 클릭 시
            // 데이터베이스에 저장
            val dbHelper = DBHelper(this, "MediDB.db", null, 1)
            val database = dbHelper.writableDatabase
            var dbCreater: DBCreater
            dbCreater = DBCreater(dbHelper, database)
            val start_date_split = start_date!!.split(". ")
            // table1 insert
            if(set_cycle==0){
                dbCreater.insertColumn_table1(
                    medi_name = medi_name!!,
                    set_cycle = set_cycle.toString(),
                    start_date = "${start_date_split[0]}-${start_date_split[1]}-${start_date_split[2].substring(0, start_date_split[2].length-1)}",
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
                dbCreater.insertColumn_table2(
                    medi_no = medi_no.toString(),
                    set_amount = item.medicine_count.toString(),
                    set_type = item.medicine_type,
                    set_date = "${simpleDateFormat.format(Date())}",
                    take_date = "null",
                    set_check = "0"
                )
            }

            // 이전화면으로 되돌아가기
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
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
            val hour = data!!.getIntExtra("hour", -1)
            val min = data.getIntExtra("min", -1)
            val count = data.getDoubleExtra("count", -1.0)
            val type = data.getStringExtra("type")
            if(hour!=-1 && min!=-1 && count!=-1.0 && type!=null) {
                val newAlarmInfo = AlarmInfo(
                    alarm_hour = hour,
                    alarm_min = min,
                    medicine_count = count,
                    medicine_type = type
                )
                alarmAdapter.addItem(newAlarmInfo)
                alarmAdapter.notifyAdapter()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로가기 핸드폰 버튼 클릭 시
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
    }
}
