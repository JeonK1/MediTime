package com.example.meditime.model

import com.example.meditime.model.ManageTimeInfo
import java.io.Serializable

data class ManageInfo(
    val medi_no: Int,
    val medi_name: String,
    val medi_date: String,
    var time_list:ArrayList<ManageTimeInfo> = ArrayList(),
    var status: Int
): Serializable