package com.example.meditime.Model

import java.io.Serializable

data class TodayInfo(
    val medi_no: Int,
    val time_no: Int,
    val medi_name: String,
    val take_date: String?,
    val record_date: String,
    val record_no: Int,
    val set_check: Int // 사용하지 않음
): Serializable


