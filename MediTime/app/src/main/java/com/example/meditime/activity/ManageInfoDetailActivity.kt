package com.example.meditime.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meditime.adapter.ManageDetailAdapter
import com.example.meditime.model.ManageInfo
import com.example.meditime.R
import kotlinx.android.synthetic.main.activity_manage_info_detail.*

class ManageInfoDetailActivity : AppCompatActivity() {

    lateinit var manageInfo_week_list: ArrayList<ManageInfo>

    // recyclerView 관련
    lateinit var manageAdapter: ManageDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_info_detail)
        globalInit()
        recyclerViewInit()
        clickListenerInit()
    }

    private fun globalInit() {
        manageInfo_week_list = intent.getSerializableExtra("manageInfo_week_list") as ArrayList<ManageInfo>
    }

    private fun recyclerViewInit() {
        manageAdapter = ManageDetailAdapter(manageInfo_week_list)
        rv_manageInfoDetail_manageList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_manageInfoDetail_manageList.adapter = manageAdapter
    }

    private fun clickListenerInit() {
        tv_manageInfoDetail_backbtn.setOnClickListener {
            finish()
        }
    }

}