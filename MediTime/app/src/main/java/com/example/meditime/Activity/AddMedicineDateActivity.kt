package com.example.meditime.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import com.example.meditime.R
import kotlinx.android.synthetic.main.activity_add_medicine_date.*
import kotlinx.android.synthetic.main.custom_cyclepicker_day_dialog.*
import kotlinx.android.synthetic.main.custom_cyclepicker_day_dialog.view.*
import kotlinx.android.synthetic.main.custom_cyclepicker_day_dialog.view.tv_cyclepickdig_day_day
import kotlinx.android.synthetic.main.custom_cyclepicker_dayofweek_dialog.view.*
import kotlinx.android.synthetic.main.custom_cyclepicker_type_dialog.view.*
import kotlinx.android.synthetic.main.custom_datepicker_dialog.view.*
import java.util.*

/*********************************
 * 화면 #3-2-1 맞춤설정
 * 의약품 추가하기 (날짜 설정)
 *********************************/

class AddMedicineDateActivity : AppCompatActivity() {

    val ADD_MEDICINE_TIME = 200
    var custom_type = -1
    var dayofweek_flag = arrayListOf(false, false, false, false, false, false, false) // 특정요일 반복 위한 선택여부 배열
    var day_flag = arrayOf(0, 0) // N일, type(일간격, 주간격, 월간격)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_date)
        listenerInit()
    }

    private fun listenerInit() {
        ib_addmedidate_backbtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시
            finish()
        }
        btn_addmedidate_everyday.setOnClickListener {
            // 매일 버튼 클릭 시
            setButtonType(true)
            ll_addmedidate_custom.visibility = View.INVISIBLE
        }
        btn_addmedidate_custom.setOnClickListener {
            // 맞춤 버튼 클릭 시
            setButtonType(false)
            ll_addmedidate_custom.visibility = View.VISIBLE
        }
        btn_addmedidate_next.setOnClickListener {
            // 다음 버튼 클릭 시
            val intent = Intent(this, AddMedicineTimeActivity::class.java)
            // Todo: 여기서의 정보 다음으로 넘기기
            intent.putExtra("medi_name", et_addmedidate_name.text.toString())
            startActivityForResult(intent, ADD_MEDICINE_TIME)
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }
        rl_addmedidate_startdate.setOnClickListener {
            // 시작 일정 있는 구간 클릭 시
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_datepicker_dialog, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            mDialogView.btn_datepickdig_ok.setOnClickListener {
                // 완료 버튼 누를 시
                val cal = Calendar.getInstance() // 혹시 요일 필요하게 되면 쓰려고 calendar를 정의하였음
                cal.set(
                    mDialogView.dp_datepickdlg_spinner.year, // 년
                    mDialogView.dp_datepickdlg_spinner.month+1, // 월
                    mDialogView.dp_datepickdlg_spinner.dayOfMonth // 일
                )
                updateDateTextView(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                mAlertDialog.dismiss()
            }
        }
        rl_addmedidate_cycletype.setOnClickListener {
            // 주기 있는 구간 클릭 시
            val cycleDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_type_dialog, null)
            val cycleBuilder = AlertDialog.Builder(this).setView(cycleDialogView)
            val cycleAlertDialog = cycleBuilder.show()
            cycleAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            cycleDialogView.btn_cyclepickdig_type1.setOnClickListener {
                // 특정 요일 클릭 시
                cycleAlertDialog.dismiss()
                val cur_dayofweek_flag = arrayListOf(false, false, false, false, false, false, false)
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_dayofweek_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val dayofweek_tv_list = arrayListOf(
                        mDialogView.tv_cyclepickdlg_dow_sun,
                        mDialogView.tv_cyclepickdlg_dow_mon,
                        mDialogView.tv_cyclepickdlg_dow_tue,
                        mDialogView.tv_cyclepickdlg_dow_wed,
                        mDialogView.tv_cyclepickdlg_dow_thu,
                        mDialogView.tv_cyclepickdlg_dow_fri,
                        mDialogView.tv_cyclepickdlg_dow_sat
                )
                for ((index, dayofweek_tv) in dayofweek_tv_list.withIndex()){
                    // 저장된 값 가져오기
                    cur_dayofweek_flag[index] = dayofweek_flag[index]
                    if(cur_dayofweek_flag[index]){
                        dayofweek_tv.setBackgroundResource(R.drawable.btn_rect_on)
                        dayofweek_tv.setTextColor(resources.getColor(R.color.colorWhite))
                    } else {
                        dayofweek_tv.setBackgroundResource(R.drawable.btn_rect_off)
                        dayofweek_tv.setTextColor(resources.getColor(R.color.colorBlueLight))
                    }
                    dayofweek_tv.setOnClickListener {
                        if(cur_dayofweek_flag[index]){
                            // 버튼 눌러져있음
                            dayofweek_tv.setBackgroundResource(R.drawable.btn_rect_off)
                            dayofweek_tv.setTextColor(resources.getColor(R.color.colorBlueLight))
                        } else {
                            // 버튼 안눌러져있음
                            dayofweek_tv.setBackgroundResource(R.drawable.btn_rect_on)
                            dayofweek_tv.setTextColor(resources.getColor(R.color.colorWhite))
                        }
                        cur_dayofweek_flag[index] = !cur_dayofweek_flag[index]
                    }
                }
                mDialogView.btn_cyclepickdlg_dow_ok.setOnClickListener{
                    // 완료 버튼 클릭 시
                    custom_type = 0
                    updateTypeTextView("특정 요일")
                    for((index, flag) in cur_dayofweek_flag.withIndex()){
                        dayofweek_flag[index] = flag // global variable 적용하기
                    }
                    mAlertDialog.dismiss()
                }

            }
            cycleDialogView.btn_cyclepickdig_type2.setOnClickListener {
                // 일 간격 클릭 시
                cycleAlertDialog.dismiss()
                var cur_day_flag = arrayOf(day_flag[0], day_flag[1]) // N일, type(일간격, 주간격, 월간격)
                val PICKER_MAX_VALUE = 10
                val PICKER_MIN_VALUE = 1
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_day_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDialogView.np_cyclepickdlg_day_numberpicker.maxValue=PICKER_MAX_VALUE
                mDialogView.np_cyclepickdlg_day_numberpicker.minValue=PICKER_MIN_VALUE
                if(day_flag[0] > 0)
                    mDialogView.np_cyclepickdlg_day_numberpicker.value = day_flag[0] // 이미 저장된 값 있으면 적용
                val daytype_tv_list = arrayListOf(
                        mDialogView.tv_cyclepickdig_day_day,
                        mDialogView.tv_cyclepickdig_day_week,
                        mDialogView.tv_cyclepickdig_day_month
                )
                for((index, daytype_tv) in daytype_tv_list.withIndex()){
                    if(index==cur_day_flag[1]){
                        setDayTypeTextView(index, mDialogView)
                    }
                    daytype_tv.setOnClickListener {
                        cur_day_flag[1] = index
                        setDayTypeTextView(cur_day_flag[1], mDialogView)
                    }
                }
                mDialogView.btn_cyclepickdig_day_ok.setOnClickListener{
                    // 완료 버튼 클릭 시
                    custom_type = 1
                    updateTypeTextView("일 간격")
                    cur_day_flag[0] = mDialogView.np_cyclepickdlg_day_numberpicker.value
                    day_flag[0] = cur_day_flag[0]
                    day_flag[1] = cur_day_flag[1]
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    fun setButtonType(is_everyday: Boolean){
        if(is_everyday){
            // 매일 버튼 눌렀을 때
            btn_addmedidate_everyday.setBackgroundResource(R.drawable.btn_rect_on)
            btn_addmedidate_everyday.setTextColor(resources.getColor(R.color.colorWhite))
            btn_addmedidate_custom.setBackgroundResource(R.drawable.btn_rect_off)
            btn_addmedidate_custom.setTextColor(resources.getColor(R.color.colorBlueLight))
        } else {
            // 맞춤 버튼 눌렀을 때
            btn_addmedidate_everyday.setBackgroundResource(R.drawable.btn_rect_off)
            btn_addmedidate_everyday.setTextColor(resources.getColor(R.color.colorBlueLight))
            btn_addmedidate_custom.setBackgroundResource(R.drawable.btn_rect_on)
            btn_addmedidate_custom.setTextColor(resources.getColor(R.color.colorWhite))
        }
    }

    fun setDayTypeTextView(idx: Int, mDialogView: View){
        // idx에 따라 우측 일간격, 주간격, 월간격 에 대한 색 변화
        val tv_list = listOf(mDialogView.tv_cyclepickdig_day_day,
                                            mDialogView.tv_cyclepickdig_day_week,
                                            mDialogView.tv_cyclepickdig_day_month)
        for (tv in tv_list){
            tv.setBackgroundResource(R.drawable.btn_rect_off)
            tv.setTextColor(resources.getColor(R.color.colorBlueLight))
        }
        tv_list[idx].setBackgroundResource(R.drawable.btn_rect_on)
        tv_list[idx].setTextColor(resources.getColor(R.color.colorWhite))
    }
    fun updateDateTextView(year:Int, month:Int, day:Int){
        // 날짜 쪽 글씨 변경 함수
        val now_year = "%d".format(year)
        val now_month = "%d".format(month)
        val now_day = "%d".format(day)
        tv_addmedidate_startdate.text = "${now_year}.${now_month}.${now_day}"
    }

    fun updateTypeTextView(type:String){
        // type 쪽 글씨 변경 함수
        tv_addmedidate_cycletype.text = type
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_MEDICINE_TIME && resultCode == Activity.RESULT_OK){
            // AddMedicineTimeActivity 에서 call back
            finish()
        }
    }

}
