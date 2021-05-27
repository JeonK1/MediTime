package com.example.meditime.Model

import java.io.Serializable

data class TodayTimeInfo(
    val medi_no: Int,
    val time_no: Int,
    val take_date: String,
    val set_date: String,
    val set_check: Int

): Serializable