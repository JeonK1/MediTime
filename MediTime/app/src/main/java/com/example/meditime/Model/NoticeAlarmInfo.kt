package com.example.meditime.Model

import java.io.Serializable

data class NoticeAlarmInfo(
    val time_no: Int = -1,
    val medi_no: Int = -1,
    val set_amount: Double,
    val set_type: String,
    val set_date: String,
    val take_date: String? = null,
    val set_check: Int = 0
):Serializable