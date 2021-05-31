package com.example.meditime.Activity

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.meditime.R

/*********************************
 * 전화 오는 화면
 * 전화 알람이 오고 있는 화면이다
 *********************************/

class CallActivity : AppCompatActivity() {

    val TAG = "CallActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        requestPermission()
        turnScreenOnAndKeyguardOff()
        clickListenerInit()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }

    private fun requestPermission() {
        // RECORD_AUDIO permission 동적허가
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun clickListenerInit() {
        val ok_btn = findViewById<ImageButton>(R.id.ib_call_yes)
        val no_btn = findViewById<ImageButton>(R.id.ib_call_no)
        ok_btn.setOnClickListener {
            // 통화 OK 버튼
            val id = intent.getIntExtra("id", -1)
            val title = intent.getStringExtra("title")
            val content = intent.getStringExtra("content")
            if(title!=null && content!=null && id!=-1) {
                Log.e(TAG, id.toString())
                Log.e(TAG, title)
                Log.e(TAG, content)
            } else {
                Log.e(TAG, "no extra")
            }

            val intent = Intent(this, CallAnswerActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("title", title)
            intent.putExtra("content", content)
            startActivity(intent)
            finish()
        }
        no_btn.setOnClickListener {
            // 통화 NO 버튼
            finish()
        }
    }

    fun Activity.turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        with(getSystemService(KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
            }
        }
    }

    fun Activity.turnScreenOffAndKeyguardOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }
    }
}