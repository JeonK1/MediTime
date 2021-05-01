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
    var medi_name:String? = null

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
        if(medi_name!=null) {
            tv_addmeditime_name.text = medi_name
        }
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
            // Todo : finish 하기 전 현재 설정값 데이터베이스에 모두 저장하기
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
            val min = data!!.getIntExtra("min", -1)
            val count = data!!.getDoubleExtra("count", -1.0)
            val type = data!!.getStringExtra("type")
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
}
