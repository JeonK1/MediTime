package com.example.meditime.Activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val ManageFragment = com.example.meditime_local.Fragment.ManageFragment()
    val NoticeFragment = com.example.meditime_local.Fragment.NoticeFragment()
    val TodayFragment = com.example.meditime_local.Fragment.TodayFragment()

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
        dbCreater.deleteAllColumns_table("table1") // example data 중복 삽입 방지
        dbCreater.deleteAllColumns_table("table2") // example data 중복 삽입 방지
        dbCreater.deleteAllColumns_table("alarm_table") // example data 중복 삽입 방지
        dbCreater.putExample()
    }

    private fun navigatorInit() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_first -> {
                    setFragment(ManageFragment)
                    setTopMenuBar("관리")
                    true
                }
                R.id.navigation_second -> {
                    setFragment(TodayFragment)
                    setTopMenuBar("오늘")
                    true
                }
                R.id.navigation_third -> {
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
