package com.example.meditime.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.core.content.ContextCompat
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.Model.NoticeInfo
import com.example.meditime.R
import com.example.meditime.Util.DowConverterFactory
import kotlinx.android.synthetic.main.activity_add_medicine_date.*
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

    // global 로 활용하는 변수
    var cur_noticeInfo = NoticeInfo()
    var type = "" // modify(수정) | add(추가)

    //데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine_date)
        dbHelperInit()
        getIntentData()
        setDesignInit2()
        okButtonInit()
        listenerInit()
    }

    private fun setDesignInit2() {
        et_addmedidate_name.setText(cur_noticeInfo.medi_name) // 약품 이름
        setButtonType(cur_noticeInfo.set_cycle==0) // 매일, 맞춤 버튼
        val tmp_list = cur_noticeInfo.start_date.split("-")
        updateDateTextView(tmp_list[0].toInt(), tmp_list[1].toInt(), tmp_list[2].toInt()) // 시작 날짜
        updateTypeTextView(cur_noticeInfo.re_type)
    }

    private fun okButtonInit() {
        // OK 버튼, 약품 이름을 작성 안했으면 비활성화하기
        if(et_addmedidate_name.text.length==0) {
            btn_addmedidate_next.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorGrayDark2)
            btn_addmedidate_next.isEnabled = false
        } else {
            btn_addmedidate_next.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.colorBlue)
            btn_addmedidate_next.isEnabled = true
        }
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(this, "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    private fun getIntentData() {
        type = intent.getStringExtra("type").toString()
        if(type=="modify"){
            // 수정을 위한 화면 일 때
            cur_noticeInfo = intent.getSerializableExtra("noticeInfo2") as NoticeInfo
        }
    }

    private fun listenerInit() {
        et_addmedidate_name.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                okButtonInit() // textview 변경 시 button 상태 변경
            }
        })
        ib_addmedidate_backbtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시
            finish()
        }
        btn_addmedidate_everyday.setOnClickListener {
            // 매일 버튼 클릭 시
            setButtonType(true)
        }
        btn_addmedidate_custom.setOnClickListener {
            // 맞춤 버튼 클릭 시
            setButtonType(false)
        }
        btn_addmedidate_next.setOnClickListener {
            // 다음 버튼 클릭 시
            // AddMedicineTimeActivity로 넘길 정보 구성하기
            val intent = Intent(this, AddMedicineTimeActivity::class.java)
            intent.putExtra("type", type)
            // noticeInfo bundle로 넘기기
            cur_noticeInfo.medi_name = et_addmedidate_name.text.toString()
            val bundle = Bundle()
            bundle.putSerializable("noticeInfo2", cur_noticeInfo)
            intent.putExtras(bundle)
            startActivityForResult(intent, ADD_MEDICINE_TIME)
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right)
        }
        rl_addmedidate_startdate.setOnClickListener {
            // 시작 날짜 클릭 시
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
                var cur_dayofweek_flag = arrayListOf(false, false, false, false, false, false, false)
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

                // 현재의 정보 cur_dayofweek_flag로 update
                if(cur_noticeInfo.re_type==0){
                    // 요일
                    cur_dayofweek_flag = DowConverterFactory.convert_int_to_arrayList(cur_noticeInfo.re_cycle)
                } else {
                    // 요일이 아님 (요일 초기화)
                    cur_dayofweek_flag = DowConverterFactory.convert_int_to_arrayList(0)
                }

                for ((index, dayofweek_tv) in dayofweek_tv_list.withIndex()){
                    // 저장된 값 가져오기
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
                    cur_noticeInfo.re_type = 0
                    updateTypeTextView(cur_noticeInfo.re_type)
                    cur_noticeInfo.re_cycle =  DowConverterFactory.convert_arrayList_to_int(cur_dayofweek_flag)
                    mAlertDialog.dismiss()
                }
            }
            cycleDialogView.btn_cyclepickdig_type2.setOnClickListener {
                // 일 간격 클릭 시
                cycleAlertDialog.dismiss() // 현재 dialog는 제거

                // 임시 저장 변수
                var cur_re_type = cur_noticeInfo.re_type
                var cur_re_cycle = cur_noticeInfo.re_cycle
                if(cur_re_type==1 && cur_re_cycle%7==0){
                    // 주에 한번 으로 변환
                    cur_re_type=1
                    cur_re_cycle/=7
                } else if (cur_re_type==1 && cur_re_cycle%7!=0) {
                    // 일에 한번 으로 변환
                    cur_re_type=0
                } else if (cur_re_type==0) {
                    // 요일에서 왔을 경우
                    cur_re_type=0
                    cur_re_cycle=1
                }

                // Dialog 생성
                val PICKER_MAX_VALUE = 10
                val PICKER_MIN_VALUE = 1
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_cyclepicker_day_dialog, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                mDialogView.np_cyclepickdlg_day_numberpicker.maxValue=PICKER_MAX_VALUE
                mDialogView.np_cyclepickdlg_day_numberpicker.minValue=PICKER_MIN_VALUE
                if(cur_re_type == 0){
                    // 일 간격 적용
                    mDialogView.np_cyclepickdlg_day_numberpicker.value = cur_re_cycle
                    setDayTypeTextView(0, mDialogView)
                } else if(cur_re_type == 1){
                    // 주 간격 적용
                    mDialogView.np_cyclepickdlg_day_numberpicker.value = cur_re_cycle
                    setDayTypeTextView(1, mDialogView)
                } else if(cur_re_type == 2){
                    // 개월 간격 적용
                    mDialogView.np_cyclepickdlg_day_numberpicker.value = cur_re_cycle
                    setDayTypeTextView(2, mDialogView)
                } else {
                    // 초기화는 일 간격의 제일 작은 value 로 설정
                    mDialogView.np_cyclepickdlg_day_numberpicker.value = PICKER_MIN_VALUE
                    setDayTypeTextView(0, mDialogView)
                }
                mDialogView.tv_cyclepickdig_day_day.setOnClickListener {
                    // 일에 한번 버튼 클릭
                    cur_re_type = 0
                    setDayTypeTextView(0, mDialogView)
                }
                mDialogView.tv_cyclepickdig_day_week.setOnClickListener {
                    // 주에 한번 버튼 클릭
                    cur_re_type = 1
                    setDayTypeTextView(1, mDialogView)
                }
                mDialogView.tv_cyclepickdig_day_month.setOnClickListener {
                    // 개월에 한번 버튼 클릭
                    cur_re_type = 2
                    setDayTypeTextView(2, mDialogView)
                }

                mDialogView.btn_cyclepickdig_day_ok.setOnClickListener{
                    // 완료 버튼 클릭 시
                    // db의 규칙(re_type=0 은 요일, re_type=1은 일 반복, re_type=2는 개월 반복) 으로 변경
                    cur_re_cycle = mDialogView.np_cyclepickdlg_day_numberpicker.value
                    if(cur_re_type==0){
                        cur_re_type=1
                    } else if(cur_re_type==1) {
                        cur_re_cycle*=7
                    }

                    updateTypeTextView(cur_re_type)

                    cur_noticeInfo.re_cycle = cur_re_cycle
                    cur_noticeInfo.re_type = cur_re_type
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    fun setButtonType(is_everyday: Boolean){
        if(is_everyday){
            // 매일 버튼 눌렀을 때
            cur_noticeInfo.set_cycle = 0
            btn_addmedidate_everyday.setBackgroundResource(R.drawable.btn_rect_on)
            btn_addmedidate_everyday.setTextColor(resources.getColor(R.color.colorWhite))
            btn_addmedidate_custom.setBackgroundResource(R.drawable.btn_rect_off)
            btn_addmedidate_custom.setTextColor(resources.getColor(R.color.colorBlueLight))
            ll_addmedidate_custom.visibility = View.INVISIBLE
        } else {
            // 맞춤 버튼 눌렀을 때
            cur_noticeInfo.set_cycle = 1
            btn_addmedidate_everyday.setBackgroundResource(R.drawable.btn_rect_off)
            btn_addmedidate_everyday.setTextColor(resources.getColor(R.color.colorBlueLight))
            btn_addmedidate_custom.setBackgroundResource(R.drawable.btn_rect_on)
            btn_addmedidate_custom.setTextColor(resources.getColor(R.color.colorWhite))
            ll_addmedidate_custom.visibility = View.VISIBLE
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
        val now_month = "%02d".format(month)
        val now_day = "%02d".format(day)
        cur_noticeInfo.start_date = "${now_year}-${now_month}-${now_day}"
        tv_addmedidate_startdate.text = "${now_year}. ${now_month}. ${now_day}. "
    }

    fun updateTypeTextView(type:Int){
        // type 쪽 글씨 변경 함수
        when {
            type==0 -> tv_addmedidate_cycletype.text = "특정 요일"
            else -> tv_addmedidate_cycletype.text = "일 간격"
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_MEDICINE_TIME && resultCode == Activity.RESULT_OK){
            // AddMedicineTimeActivity 에서 call back
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
