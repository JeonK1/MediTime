package com.example.meditime.Model

import java.io.Serializable

data class TodayInfo(
    var medi_no: Int,
    var time_no: Int,
    var set_date: String,
    var set_check: Int,
    var take_date: String
): Serializable
