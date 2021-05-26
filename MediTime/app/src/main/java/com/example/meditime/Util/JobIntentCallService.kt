package com.example.meditime.Util


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.example.meditime.Activity.CallActivity
import com.example.meditime.Activity.MainActivity
import com.example.meditime.R

class JobIntentCallService : JobIntentService() {
    companion object{
        val TAG = "JobIntentNotificationService"
        fun enqueueWork(context: Context, work: Intent){
            Log.e(TAG, "enqueueWork")
            enqueueWork(context, JobIntentCallService::class.java, 1020, work)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        Log.e(TAG, "onHandleWork")
        val CHANNEL_ID = "MEDITIME_CHANNEL";
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

        // 알림 만들기
        val mainIntent = Intent(this, MainActivity::class.java)
        val lockIntent = Intent(this, CallActivity::class.java)
        mainIntent.putExtra("id", id)
        mainIntent.putExtra("title", title)
        mainIntent.putExtra("content", content)
        lockIntent.putExtra("id", id)
        lockIntent.putExtra("title", title)
        lockIntent.putExtra("content", content)
        val mainPendingIntent = PendingIntent.getActivity(this, id, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val lockPendingIntent = PendingIntent.getActivity(this, id, lockIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(lockPendingIntent, true) // 화면이 꺼져있을 때 보여주는 pendingIntent
            .setContentIntent(mainPendingIntent) // 탭 하면 이동하는 pendingIntent
            .setAutoCancel(true) // 탭 하면 자동으로 제거

        // 알림 채널 만들기
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification Channel은 VERSION_CODES.O (API 26+) 에서만 적용됩니다.
            val channel = NotificationChannel(CHANNEL_ID, "MediTime", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Meditime"
            }
            // 채널 등록
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    override fun onStopCurrentWork(): Boolean {
        return super.onStopCurrentWork()
        Log.e(TAG, "onStopCurrentWork")
    }
}

