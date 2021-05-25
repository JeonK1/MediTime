package com.example.meditime.Model

import java.io.Serializable

data class TodayInfo(
    val medi_no: Int,
    val medi_name: String,
    var time_list:ArrayList<TodayTimeInfo> = ArrayList<TodayTimeInfo>()
): Serializable
