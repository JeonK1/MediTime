package com.example.meditime.Model

import java.io.Serializable

data class ManageTimeInfo(
    val recrod_no: Int,
    val record_date: String,
    val take_date: String?
): Serializable