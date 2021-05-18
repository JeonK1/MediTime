package com.example.meditime_local.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.AddMedicineDateActivity
import com.example.meditime.R
import kotlinx.android.synthetic.main.fragment_notice.*

/*********************************
 * 화면 #3 알림
 * 약품 알림 추가하고 확인하기
 *********************************/

class NoticeFragment : Fragment() {

    lateinit var recyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_notice, container, false)
        recyclerView = root.findViewById(R.id.notice_recyclerView)
        return root
    }

    //commit test!!
    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediList = getDummyMediItems()
        val mediAdapter = MediAdapter(mediList)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = mediAdapter

        clickListenerInit()
    }

    private fun getDummyMediItems(): ArrayList<MediInfo> {
        val outList = ArrayList<MediInfo>()
        outList.add(MediInfo(
            alarm_name = "타이레놀",
            alarm_hour = 8,
            alarm_min = 0,
            alarm_type = 0,
            is_call = true
        ))
        outList.add(MediInfo(
            alarm_name = "혈압약",
            alarm_hour = 8,
            alarm_min = 0,
            alarm_type = 0,
            is_call = true
        ))
        return outList
    }

    private fun clickListenerInit() {
        add_medi_btn.setOnClickListener {
            // AddMedicineDateActivity 실행
            val intent = Intent(context, AddMedicineDateActivity::class.java)
            startActivity(intent)
        }
    }
    /**
    override fun createNewActivity(context: Context) {
    val intent = Intent(context, NewActivity::class.java)
    startActivity(intent)
    }
     **/
    class MediAdapter(val items: ArrayList<MediInfo>) :
        RecyclerView.Adapter<MediAdapter.MyViewHolder>() {

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.item_title)
            var context: TextView = itemView.findViewById(R.id.item_context)
            var icon: ImageView = itemView.findViewById(R.id.item_icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediAdapter.MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_notice_alarm, parent, false)
            return MyViewHolder(v)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: MediAdapter.MyViewHolder, position: Int) {
            var is_am = ""
            var hour = items[position].alarm_hour
            var min = items[position].alarm_min

            if (items[position].alarm_hour > 12) {
                is_am = "오후"
                hour -= 12
            } else {
                is_am = "오전"
            }

            holder.title.text = items[position].alarm_name
            holder.context.text = "${is_am}${hour}:${"%02d".format(min)}"
        }

        fun getItems(position: Int): MediInfo {
            return items[position]
        }

        fun deleteItem(position: Int) {
            items.removeAt(position)
        }

        fun addItem(mediInfo: MediInfo) {
            items.add(mediInfo)
        }
    }

    class MediInfo(
        val alarm_name: String,
        val alarm_type: Int,
        val alarm_hour: Int,
        val alarm_min: Int,
        val is_call: Boolean
    ) {
    }


}

/*** RecyclerView 뿌려주기 끝나면 참고하면서 기능 추가하려고 러프하게 작성한 코드 ***/
//class NoticeFragment : Fragment() {
//
//    lateinit var recyclerView: RecyclerView
//    lateinit var testAdapter: TestAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        var root = inflater.inflate(R.layout.fragment_notice, container, false)
//        recyclerView = root.findViewById(R.id.notice_recyclerView)
//        return root
//    }
//
//    //DataBase에서 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        testAdapter = TestAdapter(get_dummy_data())
//        recyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = testAdapter
//        clickListenerInit()
//    }
//
//    private fun clickListenerInit() {
//        add_medi_btn.setOnClickListener {
//            // AddMedicineDateActivity 실행
//            val intent = Intent(context, AddMedicineDateActivity::class.java)
//            startActivity(intent)
//        }
//        testAdapter.itemClickListener = object : TestAdapter.OnItemClickListener {
//            override fun OnItemClick(holder: TestAdapter.MyViewHolder, view: View, position: Int) {
//                // 아이템 자체를 클릭했을 때
//                // todo : alarm_cnt, set_cycle, medi_alarm_list를 실제 데이터에 적용
//                val alarm_cnt = 2
//                val set_cycle = 0
//                val medi_alarm_list =
//                    arrayListOf(AlarmInfo(10, 0, 0.5, "정"), AlarmInfo(23, 0, 1.0, "정"))
//
//                // dialogview 만들기
//                val mDialogView =
//                    LayoutInflater.from(context).inflate(R.layout.alarm_set_dialog, null)
//                val mBuilder = AlertDialog.Builder(context).setView(mDialogView)
//                val mAlertDialog = mBuilder.show()
//                mAlertDialog.window!!.setGravity(Gravity.CENTER)
//                mDialogView.tv_alarmsetdlg_name.text = holder.test_name.text
//                mDialogView.tv_alarmsetdlg_cycle_cnt.text = "1일 ${alarm_cnt}회 복용"
//                if (set_cycle == 0)
//                    mDialogView.tv_alarmsetdlg_cycle_type.text = "매일"
//                else
//                    mDialogView.tv_alarmsetdlg_cycle_type.text =
//                        "개발 중.. " // todo : set_cycle이 1일 때 작업 하기
//                // todo : image btn 작업하기
//                mDialogView.ib_alarmsetdlg_call.setOnClickListener {
//                    // todo : call 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "call 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.ib_alarmsetdlg_bell.setOnClickListener {
//                    // todo : bell 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "bell 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.btn_alarmsetdlg_save.setOnClickListener {
//                    // todo : 완료 버튼 눌렀을 때 작업 처리하기
//                    Toast.makeText(context, "완료 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.tv_alarmsetdlg_delete.setOnClickListener {
//                    // todo : 삭제 기능
//                    Toast.makeText(context, "삭제 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                mDialogView.tv_alarmsetdlg_modify.setOnClickListener {
//                    // todo : 수정 기능
//                    Toast.makeText(context, "수정 버튼 눌렀군요. 아직 개발중입니다.", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun OnCallBtnClick(
//                holder: TestAdapter.MyViewHolder,
//                view: View,
//                position: Int
//            ) {
//                // 전화기 버튼 눌렀을 때
//                if (holder.call_flag) {
//                    holder.test_call.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//                } else {
//                    holder.test_call.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//                }
//                holder.call_flag = !holder.call_flag
//            }
//
//            override fun OnBellBtnClick(
//                holder: TestAdapter.MyViewHolder,
//                view: View,
//                position: Int
//            ) {
//                // 알람 버튼 눌렀을 때
//                if (holder.bell_flag) {
//                    holder.test_bell.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGray)
//                } else {
//                    holder.test_bell.backgroundTintList =
//                        ContextCompat.getColorStateList(holder.itemView.context, R.color.colorGreen)
//                }
//                holder.bell_flag = !holder.bell_flag
//            }
//        }
//    }
//
//    private fun get_dummy_data(): java.util.ArrayList<TestInfo> {
//        val outList = java.util.ArrayList<TestInfo>()
//        outList.add(TestInfo("타이레놀", 10, 0, 0, null, null, 1, 0))
//        outList.add(TestInfo("비타민C", 13, 0, 1, 1, 5, 0, 0))
//        outList.add(TestInfo("영양제", 10, 10, 1, 0, 42, 0, 1))
//        return outList
//    }
//}