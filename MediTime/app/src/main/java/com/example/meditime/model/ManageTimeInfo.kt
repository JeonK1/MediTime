package com.example.meditime.model

import java.io.Serializable

data class ManageTimeInfo(
    val record_no: Int,
    val record_date: String,
    val take_date: String?
): Serializable