package com.example.meditime.Util

import java.util.ArrayList
import kotlin.math.pow

class DowConverterFactory {
    // Dayofweek Converter Factory
    companion object{
        fun convert_int_to_arrayList(re_cycle: Int): ArrayList<Boolean> {
            // int 를 ArrayList<Boolean> 으로 변경
            // dayofweek_flag : 특정요일 반복 위한 선택여부 배열 (일 월 화 ... 토)
            var dayofweek_flag = arrayListOf(false, false, false, false, false, false, false)
            var re_cycle_cpy = re_cycle
            for (i in 0 until 7) {
                if (re_cycle_cpy % 2 == 1)
                    dayofweek_flag[6 - i] = true
                re_cycle_cpy /= 2
            }
            return dayofweek_flag
        }

        fun convert_arrayList_to_int(dayofweekFlag: ArrayList<Boolean>): Int {
            // ArrayList<Boolean>를 int로 변경
            var value = 0
            for (i in 0 until 7){
                if(dayofweekFlag[6-i])
                    value = value or (2.0).pow(i).toInt()
            }
            return value
        }

        fun convert_arrayList_to_string(dayofweek_flag:ArrayList<Boolean>): String{
            // ArrayList<Boolean>를 string으로 변경
            // 매주 무슨요일 반복인지 문자열 만들어주기
            var tmp_str=""
            val dayofweek_info = arrayListOf("일", "월", "화", "수", "목", "금", "토")
            var cnt = 0
            for (i in 0 until 7) {
                if (dayofweek_flag[i]) {
                    if (cnt++ == 0)
                        tmp_str += dayofweek_info[i]
                    else
                        tmp_str += ", ${dayofweek_info[i]}"
                }
            }
            return tmp_str
        }
    }
}