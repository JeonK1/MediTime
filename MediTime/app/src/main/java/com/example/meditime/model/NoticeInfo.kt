package com.example.meditime.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

@SuppressWarnings("serial")
data class NoticeInfo (
    var medi_no: Int = -1,
    var medi_name: String = "",
    var set_cycle: Int = 0,
    var start_date: String = simpleDateFormat.format(Date()),
    var re_type: Int = 0,
    var re_cycle: Int = 0,
    var call_alart: Int = 0,
    var normal_alart: Int = 1,
    var time_list:ArrayList<NoticeAlarmInfo> = ArrayList()
) : Serializable {
    override fun toString(): String {
        return "no(${medi_no}) "+
                "name(${medi_name}) "+
                "set_cycle(${set_cycle}) "+
                "start_date(${start_date}) "+
                "re_type(${re_type}) "+
                "re_cycle(${re_cycle}) "+
                "call_alart(${call_alart}) "+
                "normal_alart(${normal_alart}) "+
                "time_list(size=${time_list.size}) "
    }
}