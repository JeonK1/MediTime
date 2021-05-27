package com.example.meditime.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import com.example.meditime.R

/*********************************
 * 전화 받은 화면
 * 전화 알람이 왔을 때 전화 받기를 누르면 오는 화면이다
 *********************************/

class CallAnswerActivity : AppCompatActivity() {
    val TAG = "SecondActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_answer)

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
        Toast.makeText(this, "${id} and ${title} and ${content}", Toast.LENGTH_SHORT).show()

        clickListenerInit()
        // Todo : TTS, STT 적용하기
    }

    private fun clickListenerInit() {
        val exit_btn = findViewById<ImageButton>(R.id.ib_call_answer_exit)
        exit_btn.setOnClickListener {
            finish()
        }
    }

}