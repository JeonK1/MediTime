package com.example.meditime.Util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AlarmCallManager(val context: Context) {

    val TAG = "AlarmNotificationManager"
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    init {
        // 앱이 종료된 상태에서 백그라운드에서 작업이 돌 수 있도록, 배터리 최적화 제외 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent();
            val packageName = context.packageName
            val pm = context.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
                intent.data = Uri.parse("package:$packageName");
                context.startActivity(intent);
            }
        }
    }

    fun setAlarmRepeating(alarm_id:Int,
                          title:String = "MediTime",
                          content:String = "약 먹을 시간이에요!!",
                          start_millis:Long,
                          interval_millis:Long){
        // 알람 반복 등록
        var alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("id", alarm_id)
        alarmIntent.putExtra("title", title)
        alarmIntent.putExtra("content", content)
        val pendningIntent = PendingIntent.getBroadcast(context, alarm_id, alarmIntent, 0)
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            start_millis,
            interval_millis,
            pendningIntent
        )
        Log.e(TAG, "alarmIntent success (id: ${alarm_id})")
    }

    fun setAlarmRepeating(alarm_id:Int,
                          title:String = "MediTime",
                          content:String = "약 먹을 시간이에요!!",
                          start_date_str:String,
                          interval_millis:Long){
        // 알람 반복 등록 by String(2020-03-12 20:30:55)
        val start_date_split = start_date_str.split(" ")[0].split("-")
        val start_time_split = start_date_str.split(" ")[1].split(":")
        val start_date = Date()
        start_date.year = start_date_split[0].toInt() - 1900 // 1900 을 빼야 현재의 년도
        start_date.month = start_date_split[1].toInt() - 1 // 1을 빼야 현재의 달
        start_date.date = start_date_split[2].toInt()
        start_date.hours = start_time_split[0].toInt()
        start_date.minutes = start_time_split[1].toInt()
        start_date.seconds = 0

        setAlarmRepeating(alarm_id, title, content, start_date.time, interval_millis)
    }

    fun setAlarm(alarm_id:Int,
                 title:String = "MediTime",
                 content:String = "약 먹을 시간이에요!!",
                 start_millis:Long){
        // 알람 반복 등록
        var alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("id", alarm_id)
        alarmIntent.putExtra("title", title)
        alarmIntent.putExtra("content", content)
        val pendningIntent = PendingIntent.getBroadcast(context, alarm_id, alarmIntent, 0)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            start_millis,
            pendningIntent
        )
        Log.e(TAG, "alarmIntent success (id: ${alarm_id})")
    }

    fun setAlarm(alarm_id:Int,
                 title:String = "MediTime",
                 content:String = "약 먹을 시간이에요!!",
                 start_date_str:String){
        // 알람 반복 등록 by String(2020-03-12 20:30:55)
        val start_date = Date(start_date_str)
        setAlarm(alarm_id, title, content, start_date.time)
    }

    fun cancelAlarm_alarm_id(alarm_id: Int){
        // 알람 취소 by alarm_id
        var alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendningIntent = PendingIntent.getBroadcast(context, alarm_id, alarmIntent, 0)
        if(pendningIntent != null && alarmManager != null){
            alarmManager.cancel(pendningIntent)
            Log.e(TAG, "alarmManager cancel (id: ${alarm_id})")
        }
    }

}