package com.example.meditime.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import com.example.meditime.Model.NoticeAlarmInfo
import com.example.meditime.R
import kotlinx.android.synthetic.main.activity_add_medicine_time_set.*
import java.text.SimpleDateFormat
import java.util.*

/*********************************
 * 화면 #3-2-2 시간설정
 * 몇시에 알람울 울릴지에 대한 정보 추가하는 화면 (시간 설정)
 *********************************/

class AddMedicineTimeSetActivity : AppCompatActivity() {

    val MEDICINE_INTERVAL = 0.25 // 약 먹는 개수 증가 폭
    var medicine_count = 1.0;
    var before_text = "" // 복용량 이전 텍스트 저장용 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_time_set)
        listenerInit()
        spinnerInit()
    }

    private fun spinnerInit() {
        // spinner 초기화
        val medicine_type_list = resources.getStringArray(R.array.medicine_type) // spinner를 위한 목록 가져오기
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, medicine_type_list)
        sp_addmeditime_set_spinner.adapter = arrayAdapter
    }

    private fun listenerInit() {
        et_addmeditime_set_count.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                before_text = s.toString()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 정규표현식에 안맞으면 입력한거 하나 지워줌
                val regex = "[0-9]{0,2}([.][0-9]{0,2})?" // 소수점 (100 미만 소수점 2번째짜리까지 허용)
                if(!s.toString().matches(Regex(regex))){
                    et_addmeditime_set_count.setText(before_text) // 바꾸기 전으로 텍스트 되돌리기
                    et_addmeditime_set_count.setSelection(et_addmeditime_set_count.length()) // 커서 맨 뒤로 보내기
                } else if(s.toString() != "") {
                    medicine_count = et_addmeditime_set_count.text.toString().toDouble()
                }
            }
        })
        tv_addmeditime_set_plus.setOnClickListener {
            // + 버튼 클릭
            if(medicine_count+MEDICINE_INTERVAL < 100) {
                medicine_count += MEDICINE_INTERVAL
                et_addmeditime_set_count.setText(medicine_count.toString())
            }
        }
        tv_addmeditime_set_minus.setOnClickListener {
            // - 버튼 클릭
            if(medicine_count-MEDICINE_INTERVAL >= 0) {
                medicine_count -= MEDICINE_INTERVAL
                et_addmeditime_set_count.setText(medicine_count.toString())
            }
        }
        btn_addmeditime_set_ok.setOnClickListener {
            // 설정 버튼
            // set_date 설정
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val curDate = Date()
            curDate.hours = tp_addmeditime_set_timepicker.currentHour
            curDate.minutes = tp_addmeditime_set_timepicker.currentMinute
            curDate.seconds = 0
            
            val intent = Intent()
            val bundle = Bundle()
            bundle.putSerializable("noticeInfo3", NoticeAlarmInfo(
                set_amount = medicine_count,
                set_type = sp_addmeditime_set_spinner.selectedItem.toString(),
                set_date = simpleDateFormat.format(curDate)
            ))
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        ib_addmeditime_set_cancel.setOnClickListener {
            // 취소 버튼
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
