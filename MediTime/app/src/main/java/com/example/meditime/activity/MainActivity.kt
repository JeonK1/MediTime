package com.example.meditime.activity

import android.database.sqlite.SQLiteDatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.meditime.database.DBCreater
import com.example.meditime.database.DBHelper
import com.example.meditime.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val ManageFragment = com.example.meditime.fragment.ManageFragment()
    val NoticeFragment = com.example.meditime.fragment.NoticeFragment()
    val TodayFragment = com.example.meditime.fragment.TodayFragment()

    //DB 및 table 관련 변수
    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigatorInit()
        fragmentInit()

        //DB 및 table 생성, 예시 삽입
        dbHelper = DBHelper(this, "MediDB.db", null, 1)
        database = dbHelper.writableDatabase
        var dbCreater: DBCreater
        dbCreater = DBCreater(dbHelper, database)
        dbCreater.createTable()

        //GrowPlant
        var total = dbCreater.gettotal()
        var state = dbCreater.getstate()

        var percentage = ( total / state ).toDouble()

        if(percentage > 0.8) imageView.setImageResource(R.drawable.lv5)
        else if(percentage > 0.6 && percentage <= 0.8) imageView.setImageResource(R.drawable.lv4)
        else if(percentage > 0.4 && percentage <= 0.6) imageView.setImageResource(R.drawable.lv3)
        else if(percentage > 0.2 && percentage <= 0.4) imageView.setImageResource(R.drawable.lv2)
        else imageView.setImageResource(R.drawable.lv1)
    }

    private fun navigatorInit() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_manage -> {
                    setFragment(ManageFragment)
                    setTopMenuBar("관리")
                    true
                }
                R.id.navigation_today -> {
                    setFragment(TodayFragment)
                    setTopMenuBar("오늘")
                    true
                }
                R.id.navigation_notice -> {
                    setFragment(NoticeFragment)
                    setTopMenuBar("알림")
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setFragment(curFragment: Fragment){
        val fragment = supportFragmentManager.beginTransaction()
        fragment.addToBackStack(null)
        fragment.replace(R.id.mainFragment, curFragment)
        fragment.commit()
    }

    private fun setTopMenuBar(str:String){
        tv_topMenu.text = str
    }

    private fun fragmentInit() {
        setFragment(TodayFragment)
        setTopMenuBar("오늘")
    }
}
