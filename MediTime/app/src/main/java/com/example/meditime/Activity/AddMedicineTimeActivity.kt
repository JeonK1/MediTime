package com.example.meditime.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.*
import kotlinx.android.synthetic.main.activity_add_medicine_time.*

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 목록이 있는 화면
 *********************************/

class AddMedicineTimeActivity : AppCompatActivity() {

    val ADD_MEDICINE_TIME_SET = 201
    lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time)
        recyclerViewInit()
        listenerInit()
    }

    private fun recyclerViewInit() {
        val alarmList = ArrayList<AlarmInfo>()
        alarmAdapter = AlarmAdapter(alarmList)
        rv_addmeditime_container.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_addmeditime_container.adapter = alarmAdapter
    }

    private fun listenerInit() {
        ib_addmeditime_backbtn.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
        }
        btn_addmeditime_ok.setOnClickListener {
            // 완료 버튼 클릭 시
            // Todo : finish 하기 전 현재 설정값 데이터베이스에 모두 저장하기
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        ib_addmeditime_addbtn.setOnClickListener {
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
            val min = data!!.getIntExtra("min", -1)
            val count = data!!.getDoubleExtra("count", -1.0)
            val type = data!!.getStringExtra("type") // Todo : DB가 확정나지 않아서 일단 활용 방안은 보류
            if(hour!=-1 && min!=-1 && count!=-1.0 && type!=null) {
                val newAlarmInfo = AlarmInfo(
                    alarm_hour = hour,
                    alarm_min = min,
                    medicine_count = count,
                    medicin_type = type
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

//    private fun getDummyAlarmItems(): ArrayList<AlarmInfo> {
//        val dummy_list = ArrayList<AlarmInfo>()
//        dummy_list.add(AlarmInfo(
//                alarm_hour = 8,
//                alarm_min = 0,
//                medicine_count = 1.0
//        ))
//        dummy_list.add(AlarmInfo(
//                alarm_hour = 9,
//                alarm_min = 30,
//                medicine_count = 1.5
//        ))
//        return dummy_list
//    }
}
