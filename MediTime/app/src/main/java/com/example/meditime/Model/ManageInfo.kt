package com.example.meditime

import com.example.meditime.Model.ManageTimeInfo
import com.example.meditime.Model.TodayTimeInfo
import java.io.Serializable

data class ManageInfo(
    val medi_no: Int,
    val medi_name: String,
    var time_list:ArrayList<ManageTimeInfo> = ArrayList<ManageTimeInfo>()
): Serializable