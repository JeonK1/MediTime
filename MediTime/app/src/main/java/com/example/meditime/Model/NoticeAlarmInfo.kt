package com.example.meditime.Model

import java.io.Serializable

data class NoticeAlarmInfo(
    var time_no: Int = -1,
    var medi_no: Int = -1,
    var set_amount: Double,
    var set_type: String,
    var set_date: String,
    var take_date: String? = null,
    var set_check: Int = 0
):Serializable