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
import com.example.meditime.Database.DBCreater
import com.example.meditime.Database.DBHelper
import com.example.meditime.R
import java.util.*

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

        // 알람이 개월 단위일 때, 다음 개월 알람 설정
        val dbHelper = DBHelper(this, "MediDB.db", null, 1)
        val dbCreater = DBCreater(dbHelper, dbHelper.writableDatabase)
        val cur_noticeAlarmInfo = dbCreater.get_noticeAlarmInfo_by_alarm_no(id)
        val cur_noticeInfo = dbCreater.get_noticeInfo_by_medi_no(cur_noticeAlarmInfo.medi_no)
        if(cur_noticeInfo.re_type==2){
            // 개월 단위 알람이면
            val alarmCallManager = AlarmCallManager(this)

            // N개월 뒤 구하기
            val start_time_split = cur_noticeAlarmInfo.set_date.split(" ")[1].split(":")
            val start_date = Date()
            start_date.month += cur_noticeInfo.re_cycle // 1을 빼야 현재의 달, N개월 뒤로 재설정
            start_date.hours = start_time_split[0].toInt()
            start_date.minutes = start_time_split[1].toInt()
            start_date.seconds = 0

            // 알람 재설정
            val alarm_no = dbCreater.insert_alarm(time_no = cur_noticeAlarmInfo.time_no)
            alarmCallManager.setAlarm(
                alarm_id = alarm_no.toInt(),
                start_millis = start_date.time
            )
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

        // Todo: 현재 alarm_id의 normal_alart, call_alart 상황에 따라 Notification 다르게 실행하도록 구현
        // Todo: call_alart=1, normal_alart=1 >> setFullScreenIntent 까지
        // Todo: call_alart=0, normal_alart=1 >> setFullScreenIntent 제외
        // Todo: call_alart=0, normal_alart=0 >> Notification 출력 X
        // Todo: 기타 상황은 존재하지 않도록 구현
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

