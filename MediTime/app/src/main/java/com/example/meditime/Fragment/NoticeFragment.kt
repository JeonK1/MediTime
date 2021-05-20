package com.example.meditime_local.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.AddMedicineDateActivity
import com.example.meditime.Adapter.AlarmSetDlgAdapter
import com.example.meditime.Adapter.NoticeAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.NoticeInfo
import com.example.meditime.R
import kotlinx.android.synthetic.main.alarm_set_dialog.view.*
import kotlinx.android.synthetic.main.fragment_notice.*
import kotlin.properties.Delegates

/*********************************
 * 화면 #3 알림
 * 약품 알림 추가하고 확인하기
 *********************************/

class NoticeFragment : Fragment() {

    val ADD_MEDICINE = 103
    val ADD_MEDICINE_MODIFY = 104

    //알람 Fragment RecyclerView를 위한 변수들
    lateinit var recyclerView: RecyclerView
    lateinit var mediAdapter: NoticeAdapter

    //데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    companion object {
        var item = ArrayList<NoticeInfo>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Fragment 화면, RecyclerView 정의
        var root = inflater.inflate(R.layout.fragment_notice, container, false)
        recyclerView = root.findViewById(R.id.notice_recyclerView)
        return root
    }

    //View가 생성되고 나서 NoticeFragment를 위한 준비 부분
    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들어 화면에 뿌려주기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelperInit()
        fragment_init()
    }

    fun fragment_init(){
        updateNoticeItems()
        recyclerViewInit()
        clickListenerInit()
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(getContext(), "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    fun recyclerViewInit() {
        mediAdapter = NoticeAdapter(item)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mediAdapter
    }

    //사용자가 추가한 알림들 update
    fun updateNoticeItems() {
        //사용자 알림 내용들을 데이터베이스에서 가져와 item에 추가
        item.clear()
        val cursor: Cursor = dbCreater.JoinAndSelectAll()

        if (cursor.getCount() == 0) {
            Log.d("cursor1_t1: ", "데이터 없음")
        }

        while (cursor.moveToNext()) {
            //알람 데이터들을 위한 변수들
            var medi_name: String
            var medi_no by Delegates.notNull<Int>()
            var time_no by Delegates.notNull<Int>()
            var set_cycle by Delegates.notNull<Int>()
            var set_date by Delegates.notNull<String>()
            var set_amount by Delegates.notNull<Int>()
            var re_type by Delegates.notNull<Int>()
            var re_cycle by Delegates.notNull<Int>()
            var call_alart by Delegates.notNull<Int>()
            var normal_alart by Delegates.notNull<Int>()

            medi_name = cursor.getString(cursor.getColumnIndex("medi_name"))
            medi_no = cursor.getInt(cursor.getColumnIndex("medi_no"))
            time_no = cursor.getInt(cursor.getColumnIndex("time_no"))
            set_cycle = cursor.getInt(cursor.getColumnIndex("set_cycle"))
            set_date = cursor.getString(cursor.getColumnIndex("set_date"))
            set_amount = cursor.getInt(cursor.getColumnIndex("set_amount"))
            re_type = cursor.getInt(cursor.getColumnIndex("re_type"))
            re_cycle = cursor.getInt(cursor.getColumnIndex("re_cycle"))
            call_alart = cursor.getInt(cursor.getColumnIndex("call_alart"))
            normal_alart = cursor.getInt(cursor.getColumnIndex("normal_alart"))

            item.add(
                NoticeInfo(
                    medi_name, medi_no, time_no, set_cycle, set_date,
                    set_amount, re_type, re_cycle, call_alart, normal_alart
                )
            )
        }
        cursor.close()
    }

    private fun clickListenerInit() {
        add_medi_btn.setOnClickListener {
            // 약품추가 버튼 클릭 시, AddMedicineDateActivity 실행
            val intent = Intent(context, AddMedicineDateActivity::class.java)
            intent.putExtra("type", "add")
            startActivityForResult(intent, ADD_MEDICINE)
        }
        mediAdapter.itemClickListener = object : NoticeAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: NoticeAdapter.MyViewHolder,
                view: View,
                position: Int
            ) {
                // 아이템 자체를 클릭했을 때
                val medi_no = item[position].medi_no
                val set_cycle = item[position].set_cycle
                val re_type = item[position].re_type
                val re_cycle = item[position].re_cycle
                val alarm_cnt = dbCreater.get_alarm_cnt_by_medi_no(medi_no)

                // dialogview 만들기
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.alarm_set_dialog, null)
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setGravity(Gravity.CENTER)

                // recyclerView
                val medi_alarm_list = dbCreater.get_alarm_by_medi_no(medi_no)
                val alarmSetDlgAdapter = AlarmSetDlgAdapter(medi_alarm_list)
                mDialogView.rv_alarmsetdlg_cycle.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                mDialogView.rv_alarmsetdlg_cycle.adapter = alarmSetDlgAdapter

                // 내용 적용
                mDialogView.tv_alarmsetdlg_name.text = holder.medi_name.text
                mDialogView.tv_alarmsetdlg_cycle_cnt.text = "1일 ${alarm_cnt}회 복용"
                if (set_cycle == 0)
                    mDialogView.tv_alarmsetdlg_cycle_type.text = "매일"
                else {
                    when {
                        re_type == 0 && re_cycle != null -> {
                            // 요일 반복
                            val dayofweek_info = arrayListOf("일", "월", "화", "수", "목", "금", "토")
                            val dayofweek_flag = convert_Int_to_arrayList(re_cycle)
                            // 매주 무슨요일 반복인지 문자열 만들어주기
                            var tmp_str = "매주["
                            var cnt = 0
                            for (i in 0 until 7) {
                                if (dayofweek_flag[i]) {
                                    if (cnt++ == 0)
                                        tmp_str += dayofweek_info[i]
                                    else
                                        tmp_str += ", ${dayofweek_info[i]}"
                                }
                            }
                            tmp_str += "]"
                            mDialogView.tv_alarmsetdlg_cycle_type.text = tmp_str
                        }
                        re_type == 1 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}일 간격" // 일 반복
                        re_type == 2 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}개월 간격" // 개월 반복
                    }
                    when {
                        re_type == 0 && re_cycle != null -> {
                            // 요일 반복
                            val dayofweek_info = arrayListOf("일", "월", "화", "수", "목", "금", "토")
                            val dayofweek_flag = convert_Int_to_arrayList(re_cycle)
                            // 매주 무슨요일 반복인지 문자열 만들어주기
                            var tmp_str = "매주["
                            var cnt = 0
                            for (i in 0 until 7) {
                                if (dayofweek_flag[i]) {
                                    if (cnt++ == 0)
                                        tmp_str += dayofweek_info[i]
                                    else
                                        tmp_str += ", ${dayofweek_info[i]}"
                                }
                            }
                            tmp_str += "]"
                            mDialogView.tv_alarmsetdlg_cycle_type.text = tmp_str
                        }
                        re_type == 1 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}일 간격" // 일 반복
                        re_type == 2 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}개월 간격" // 개월 반복
                    }
                }
                var cur_call_flag = holder.call_flag
                var cur_bell_flag = holder.bell_flag
                if (cur_call_flag) {
                    mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }
                if (cur_bell_flag) {
                    mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }
                mDialogView.ib_alarmsetdlg_call.setOnClickListener {
                    // call 눌렀을 때
                    // 변환
                    cur_call_flag = !cur_call_flag
                    if (cur_call_flag && cur_bell_flag) {
                        // 둘다 선택되는 경우 방지
                        cur_bell_flag = !cur_bell_flag
                    }
                    // 색상변환
                    if (cur_call_flag) {
                        mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }
                    if (cur_bell_flag) {
                        mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }

                }

                mDialogView.ib_alarmsetdlg_bell.setOnClickListener {
                    // bell 눌렀을 때
                    // 변환
                    cur_bell_flag = !cur_bell_flag
                    if (cur_call_flag && cur_bell_flag) {
                        // 둘다 선택되는 경우 방지
                        cur_call_flag = !cur_call_flag
                    }
                    // 색상변환
                    if (cur_call_flag) {
                        mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        mDialogView.ib_alarmsetdlg_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }
                    if (cur_bell_flag) {
                        mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        mDialogView.ib_alarmsetdlg_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }
                }

                mDialogView.btn_alarmsetdlg_save.setOnClickListener {
                    // recyclerview item에 적용
                    holder.call_flag = cur_call_flag
                    holder.bell_flag = cur_bell_flag

                    // recyclerview design에 적용
                    if (holder.call_flag) {
                        holder.medi_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        holder.medi_call.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }
                    if (holder.bell_flag) {
                        holder.medi_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                    } else {
                        holder.medi_bell.backgroundTintList =
                            ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                    }

                    // DB에 적용
                    val medi_no = item[position].medi_no
                    dbCreater.set_call_state(medi_no, holder.call_flag)
                    dbCreater.set_normal_state(medi_no, holder.bell_flag)
                    mAlertDialog.cancel() // dialog 종료
                }

                mDialogView.tv_alarmsetdlg_delete.setOnClickListener {
                    // 삭제 버튼 클릭 시
                    val medi_no = item[position].medi_no
                    val medi_name = item[position].medi_name
                    dbCreater.delete_medicine_by_id(medi_no)
                    Toast.makeText(context, "${medi_name} 알람이 제거되었습니다.", Toast.LENGTH_SHORT).show()
                    fragment_init()
                    mAlertDialog.cancel() // dialog 종료
                }

                mDialogView.tv_alarmsetdlg_modify.setOnClickListener {
                    // 수정 버튼 클릭 시
                    mAlertDialog.cancel() // dialog 종료
                    val intent = Intent(context, AddMedicineDateActivity::class.java)
                    intent.putExtra("type", "modify")
                    intent.putExtra("medi_no", medi_no)
                    startActivityForResult(intent, ADD_MEDICINE_MODIFY)
                }
            }

            override fun OnCallBtnClick(
                holder: NoticeAdapter.MyViewHolder,
                view: View,
                position: Int
            ) {
                // 전화기 버튼 눌렀을 때
                // 변환
                holder.call_flag = !holder.call_flag
                if (holder.call_flag && holder.bell_flag) {
                    // 둘다 선택되는 경우 방지
                    holder.bell_flag = !holder.bell_flag
                }

                // 디자인 적용
                if (holder.call_flag) {
                    holder.medi_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    holder.medi_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }
                if (holder.bell_flag) {
                    holder.medi_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    holder.medi_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }

                // DB에 적용
                val medi_no = item[position].medi_no
                dbCreater.set_call_state(medi_no, holder.call_flag)
                dbCreater.set_normal_state(medi_no, holder.bell_flag)
            }

            override fun OnBellBtnClick(
                holder: NoticeAdapter.MyViewHolder,
                view: View,
                position: Int
            ) {
                // 알람 버튼 눌렀을 때
                // 변환
                holder.bell_flag = !holder.bell_flag
                if (holder.call_flag && holder.bell_flag) {
                    // 둘다 선택되는 경우 방지
                    holder.call_flag = !holder.call_flag
                }

                // 디자인 적용
                val medi_no = item[position].medi_no
                if (holder.call_flag) {
                    holder.medi_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    holder.medi_call.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }
                if (holder.bell_flag) {
                    holder.medi_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
                } else {
                    holder.medi_bell.backgroundTintList =
                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
                }

                // DB에 적용
                dbCreater.set_call_state(medi_no, holder.call_flag)
                dbCreater.set_normal_state(medi_no, holder.bell_flag)
            }
        }
    }

    fun convert_Int_to_arrayList(re_cycle: Int): ArrayList<Boolean> {
        var dayofweek_flag = arrayListOf(
            false,
            false,
            false,
            false,
            false,
            false,
            false
        ) // 특정요일 반복 위한 선택여부 배열 (일 ~ 토)
        var re_cycle_cpy = re_cycle
        for (i in 0 until 7) {
            if (re_cycle_cpy % 2 == 1)
                dayofweek_flag[6 - i] = true
            re_cycle_cpy /= 2
        }
        return dayofweek_flag
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_MEDICINE) {
            // 약품 추가 버튼
            if (resultCode == Activity.RESULT_OK) {
                // 완료버튼을 눌러서 나온 경우
                fragment_init()
                Toast.makeText(context, "새로운 알림이 등록되었습니다", Toast.LENGTH_SHORT).show()
            }
        } else if(requestCode == ADD_MEDICINE_MODIFY){
            // 약품 수정 버튼
            if (resultCode == Activity.RESULT_OK) {
                // 완료버튼을 눌러서 나온 경우
                fragment_init()
                Toast.makeText(context, "알림이 정상적으로 수정되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

}