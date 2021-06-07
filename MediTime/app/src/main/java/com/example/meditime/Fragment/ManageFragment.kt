package com.example.meditime_local.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditime.Activity.ManageInfoDetailActivity
import com.example.meditime.Adapter.ManageAdapter
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.R

/*********************************
 * 화면 #2 관리
 * 약품 복용 일지 확인
 *********************************/


class ManageFragment : Fragment() {


    // 관리 화면 RecyclerView를 위한 변수들
    lateinit var recyclerView: RecyclerView
    lateinit var alarmAdapter: ManageAdapter

    // 데이터 베이스 사용
    lateinit var dbHelper: DBHelper
    lateinit var dbCreater: DBCreater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_manage, container, false)
        recyclerView = root.findViewById(R.id.manage_recyclerView)
        return root
    }

    //DataBase에서 복용한 약품에 대한 정보를 받아와 List를 만들고 화면에 뿌려주어야 합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelperInit()
        fragment_init()
    }
    fun fragment_init(){
        recyclerViewInit()
        clickListenerInit()
    }

    private fun dbHelperInit() {
        dbHelper = DBHelper(getContext(), "MediDB.db", null, 1)
        dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
    }

    fun recyclerViewInit() {
        alarmAdapter = ManageAdapter(dbCreater.get_manageInfo_week())
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = alarmAdapter
    }

    private fun clickListenerInit() {
        alarmAdapter.itemClickListener = object : ManageAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: ManageAdapter.MyViewHolder,
                view: View,
                position: Int
            ) {
                // item click
                val bundle = Bundle()
                bundle.putSerializable("manageInfo_week_list", alarmAdapter.items[position])
                val intent = Intent(context, ManageInfoDetailActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }
}