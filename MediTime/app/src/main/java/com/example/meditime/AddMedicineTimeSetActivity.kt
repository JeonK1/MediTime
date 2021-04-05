package com.example.meditime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_medicine_time_set.*

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 추가하는 화면 (시간 설정)
 *********************************/

class AddMedicineTimeSetActivity : AppCompatActivity() {

    val MEDICINE_INTERVAL = 0.5 // 약 먹는 개수 증가 폭
    var medicine_count = 1.0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time_set)
        listenerInit()
    }

    private fun listenerInit() {
        tv_addmeditime_set_plus.setOnClickListener {
            // + 버튼 클릭
            medicine_count += MEDICINE_INTERVAL
            tv_addmeditime_set_count.text = medicine_count.toString()
        }
        tv_addmeditime_set_minus.setOnClickListener {
            // - 버튼 클릭
            medicine_count -= MEDICINE_INTERVAL
            tv_addmeditime_set_count.text = medicine_count.toString()
        }
        btn_addmeditime_set_ok.setOnClickListener {
            // 설정 버튼
            // Todo : recyclerView 만들 수 있도록 데이터 잘 데리고 나오기
            finish()
        }
        btn_addmeditime_set_cancel.setOnClickListener {
            // 취소 버튼
            finish()
        }
    }
}
