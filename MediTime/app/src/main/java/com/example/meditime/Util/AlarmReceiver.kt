package com.example.meditime.Util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class AlarmReceiver : BroadcastReceiver() {
    val TAG = "AlarmReceiver"

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        // alarm service 생성 후 시작
        Log.e(TAG, "onReceive")

        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        if(title!=null && content!=null && id!=-1) {
            Log.e(TAG, id.toString())
            Log.e(TAG, title)
            Log.e(TAG, content)
            val serviceIntent = Intent(context, JobIntentCallService::class.java)
            serviceIntent.putExtra("id", id)
            serviceIntent.putExtra("title", title)
            serviceIntent.putExtra("content", content)
            JobIntentCallService.enqueueWork(context, serviceIntent)
        } else {
            Log.e(TAG, "no extra")
        }
    }
}