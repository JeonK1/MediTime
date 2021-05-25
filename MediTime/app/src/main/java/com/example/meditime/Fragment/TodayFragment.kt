package com.example.meditime_local.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Adapter.TodayAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.R

/*********************************
 * 화면 #1 오늘
 * 해당일자 약품 알림 확인하기
 *********************************/

class TodayFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var alarmAdapter: TodayAdapter

    // 데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_today, container, false)
        recyclerView = root.findViewById(R.id.today_recyclerView)
        return root
    }

    //DataBase에서 해당일자 약품 알림에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelperInit()
        fragment_init()
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(getContext(), "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    fun fragment_init(){
        recyclerViewInit()
        /*clickListenerInit()*/
    }

    fun recyclerViewInit() {
//        alarmAdapter = TodayAdapter(dbCreater.get_noticeinfo2_all())
//        recyclerView.layoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        recyclerView.adapter = alarmAdapter*/
    }
}