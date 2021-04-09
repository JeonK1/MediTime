package com.example.meditime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val ManageFragment = com.example.meditime_local.Fragment.ManageFragment()
    val NoticeFragment = com.example.meditime_local.Fragment.NoticeFragment()
    val TodayFragment = com.example.meditime_local.Fragment.TodayFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigatorInit()
        fragmentInit()
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
