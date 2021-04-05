package com.example.meditime

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.activity_add_medicine_date.*
import kotlinx.android.synthetic.main.custom_cyclepicker_day_dialog.view.*
import kotlinx.android.synthetic.main.custom_cyclepicker_dayofweek_dialog.view.*
import kotlinx.android.synthetic.main.custom_cyclepicker_type_dialog.view.*

/*********************************
 * 화면 #3-2-1 맞춤설정
 * 의약품 추가하기 (날짜 설정)
 *********************************/

class AddMedicineDateActivity : AppCompatActivity() {

    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_date)
        listenerInit()
    }

    private fun listenerInit() {
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            // 날자 설정 완료 시
            // Todo : 데이터베이스에 넣어줄 데이터 갱신
            // Todo : tv_addmedidate_startdate 값 날짜 형식 맞춰 갱신
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
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }
        rl_addmedidate_startdate.setOnClickListener {
            // 시작 일정 있는 구간 클릭 시
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_datepicker_dialog, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_dayofweek_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDialogView.btn_cyclepickdlg_dow_ok.setOnClickListener{
                    mAlertDialog.dismiss()
                    // Todo : 데이터베이스에 넣어줄 데이터 갱신
                    // Todo : tv_addmedidate_cycletype 값 바꿔주기
                }
            }
            cycleDialogView.btn_cyclepickdig_type2.setOnClickListener {
                // 일 간격 클릭 시
                cycleAlertDialog.dismiss()
                val PICKER_MAX_VALUE = 10
                val PICKER_MIN_VALUE = 1
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_day_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDialogView.np_cyclepickdlg_day_numberpicker.maxValue=PICKER_MAX_VALUE
                mDialogView.np_cyclepickdlg_day_numberpicker.minValue=PICKER_MIN_VALUE
                mDialogView.btn_cyclepickdig_day_ok.setOnClickListener{
                    mAlertDialog.dismiss()
                    // Todo : 데이터베이스에 넣어줄 데이터 갱신
                    // Todo : tv_addmedidate_cycletype 값 바꿔주기
                }
            }
            cycleDialogView.btn_cyclepickdig_type3.setOnClickListener {
                // 되풀이 주기 버튼 클릭 시
                cycleAlertDialog.dismiss()
            }
        }
    }

    fun setButtonType(is_everyday: Boolean){
        if(is_everyday){
            // 매일 버튼 눌렀을 때
            btn_addmedidate_everyday.setBackgroundColor(Color.parseColor("#0000cc"))
            btn_addmedidate_custom.setBackgroundColor(Color.parseColor("#dddddd"))
        } else {
            // 맞춤 버튼 눌렀을 때
            btn_addmedidate_everyday.setBackgroundColor(Color.parseColor("#dddddd"))
            btn_addmedidate_custom.setBackgroundColor(Color.parseColor("#0000cc"))
        }
    }
}
