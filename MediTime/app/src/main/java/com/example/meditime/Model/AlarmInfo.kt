package com.example.meditime.Model

import java.io.Serializable

@SuppressWarnings("serial")
class AlarmInfo(val alarm_hour:Int, val alarm_min:Int, val medicine_count:Double, val medicine_type:String):Serializable {
}