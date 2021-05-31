package com.example.meditime.Model

import java.io.Serializable

data class TodayInfo(
    val medi_no: Int,
    val time_no: Int,
    val medi_name: String,
    val take_date: String,
    val set_date: String,
    val set_check: Int

): Serializable


