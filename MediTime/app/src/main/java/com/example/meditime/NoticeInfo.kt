package com.example.meditime

class NoticeInfo(
    val medi_name: String,
    val medi_no: Int,
    val time_no: Int,
    val set_cycle: Int,
    val set_date: String,
    val set_amount: Int,
    val re_type: Int,
    val re_cycle: Int,
    val call_alart: Int,
    val normal_alart: Int
) {
}

class NoticeInfo_t1(
    val medi_no: Int,
    val medi_name: String,
    val set_cycle: Int,
    val re_type: Int,
    val re_cycle: Int,
    val call_alart: Int,
    val normal_alart: Int
) {
}

class NoticeInfo_t2(
    val set_date: String,
    val set_amount: Int
) {
}