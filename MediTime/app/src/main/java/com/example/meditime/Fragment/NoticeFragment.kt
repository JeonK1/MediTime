package com.example.meditime_local.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import com.example.meditime.R
import com.example.meditime.Util.AlarmCallManager
import com.example.meditime.Util.DowConverterFactory
import kotlinx.android.synthetic.main.alarm_set_dialog.view.*
import kotlinx.android.synthetic.main.fragment_notice.*

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

    //AlarmManager
    lateinit var alarmCallManager: AlarmCallManager

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
        globalInit()
        fragment_init()
    }

    private fun globalInit() {
        // AlarmCallManager
        alarmCallManager = AlarmCallManager(context!!)

        // Database
        dbHelper = DBHelper(context, "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    fun recyclerViewInit() {
        mediAdapter = NoticeAdapter(dbCreater.get_noticeinfo2_all())
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mediAdapter
    }

    fun fragment_init(){
        recyclerViewInit()
        clickListenerInit()
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
                val medi_no = mediAdapter.items.get(position).medi_no
                val set_cycle = mediAdapter.items.get(position).set_cycle
                val re_type = mediAdapter.items.get(position).re_type
                val re_cycle = mediAdapter.items.get(position).re_cycle
                val alarm_cnt = mediAdapter.items.get(position).time_list.size

                // dialogview 만들기
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.alarm_set_dialog, null)
                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                mAlertDialog.window!!.setGravity(Gravity.CENTER)

                // recyclerView
                val alarmSetDlgAdapter = AlarmSetDlgAdapter(mediAdapter.items.get(position).time_list)
                mDialogView.rv_alarmsetdlg_cycle.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                mDialogView.rv_alarmsetdlg_cycle.adapter = alarmSetDlgAdapter

                // 내용 적용
                mDialogView.tv_alarmsetdlg_name.text = holder.medi_name.text
                mDialogView.tv_alarmsetdlg_cycle_cnt.text = "1회 ${alarm_cnt}번 복용"
                if (set_cycle == 0)
                    mDialogView.tv_alarmsetdlg_cycle_type.text = "매일"
                else {
                    when {
                        re_type == 0 && re_cycle != null -> {
                            // 요일 반복
                            val dayofweek_flag = DowConverterFactory.convert_int_to_arrayList(re_cycle)
                            mDialogView.tv_alarmsetdlg_cycle_type.text =
                                "매주[${DowConverterFactory.convert_arrayList_to_string(dayofweek_flag)}]"
                        }
                        re_type == 1 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}일 간격" // 일 반복
                        re_type == 2 -> mDialogView.tv_alarmsetdlg_cycle_type.text =
                            "${re_cycle}개월 간격" // 개월 반복
                    }
                }

                // call, bell 디자인 적용
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
                    val medi_no = mediAdapter.items.get(position).medi_no
                    dbCreater.set_call_state(medi_no, holder.call_flag)
                    dbCreater.set_normal_state(medi_no, holder.bell_flag)
                    mAlertDialog.cancel() // dialog 종료
                }

                mDialogView.tv_alarmsetdlg_delete.setOnClickListener {
                    // 삭제 버튼 클릭 시
                    val medi_no = mediAdapter.items.get(position).medi_no
                    val medi_name = mediAdapter.items.get(position).medi_name
                    for (cur_time_no in dbCreater.get_time_no_by_medi_no(medi_no)){
                        for (cur_alarm_no in dbCreater.get_alarm_no_by_time_no(cur_time_no)){
                            dbCreater.set_delete_alarm_by_alarm_no(cur_alarm_no)
                            alarmCallManager.cancelAlarm_alarm_id(cur_alarm_no)
                        }
                    }
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
                    val bundle = Bundle()
                    bundle.putSerializable("noticeInfo2", mediAdapter.items.get(position))
                    intent.putExtras(bundle)
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
                val medi_no = mediAdapter.items.get(position).medi_no
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
                val medi_no = mediAdapter.items.get(position).medi_no
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