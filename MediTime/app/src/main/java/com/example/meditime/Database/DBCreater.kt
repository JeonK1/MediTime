package com.example.meditime.Database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.meditime.ManageInfo
import com.example.meditime.Model.*
import com.example.meditime.Util.DowConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBCreater(dbHelper: DBHelper, private val db: SQLiteDatabase) {

    fun createTable() {
        //table1 생성
        var query1 = "CREATE TABLE IF NOT EXISTS table1 ( " +
                "medi_no INTEGER PRIMARY KEY, " + //약 인덱스
                "medi_name CHAR(50), " + //약 이름
                "set_cycle INTEGER, " + //복용 주기 : 0, 1
                "start_date DATE, " + //시작 날짜 : yy-mm-dd
                "re_type INTEGER, " + //반복 타입 : 0(요일), 1(일), 2(개월)
                "re_cycle INTEGER, " + //반복 주기 : 2진수 변환
                "call_alart INTEGER, " + //전화 알람 : 0, 1
                "normal_alart INTEGER " + //일반 알람 : 0, 1
                //복용 주기가 0일 경우 반복 타입과 반복 주기가 null 값이 되게 설정해야 함
                //전화 알람 default 값을 0, 일반 알람 default 값을 1로 하는 것 고려해보기
                " );"
        db.execSQL(query1)

        //table2 생성
        var query2 = "CREATE TABLE IF NOT EXISTS table2 ( " +
                "time_no INTEGER PRIMARY KEY, " + //시간 인덱스
                "medi_no INTEGER, " + //table1과 비교해서 사용
                "set_amount DOUBLE, " + //복용량
                "set_type CHAR(50), " + //복용 타입 : 정, 봉지, ...
                "set_date DATETIME, " + //복용 시간 : yy-mm-dd hh:mm:ss
                "take_date DATETIME, " + //섭취 시간 : yy-mm-dd hh:mm:ss
                "set_check INTEGER " + //복용 여부 : 0, 1
                " );"
        //섭취 시간의 default 값 생각해보기
        //복용 여부의 default 값을 0으로 하는 것 고려해보기
        db.execSQL(query2)

        //table3 생성
        var query3 = "CREATE TABLE IF NOT EXISTS table3 ( " +
                "medi_name CHAR(50), " + //약 이름
                "medi_no INTEGER, " + //table1과 비교해서 사용
                "time_no INTEGER, " + //시간 인덱스
                "set_cycle INTEGER, " + //복용 주기 : 0, 1
                "start_date DATE, " + //시작 날짜 : yy-mm-dd
                "re_type INTEGER, " + //반복 타입 : 0(요일), 1(일), 2(개월)
                "re_cycle INTEGER, " + //반복 주기 : 2진수 변환
                "set_amount DOUBLE, " + //복용량
                "set_type CHAR(50), " + //복용 타입 : 정, 봉지, ...
                "set_date DATETIME, " + //복용 시간 : yy-mm-dd hh:mm:ss
                "take_date DATETIME, " + //섭취 시간 : yy-mm-dd hh:mm:ss
                "set_check INTEGER, " + //복용 여부 : 0, 1
                "call_alart INTEGER, " + //전화 알람 : 0, 1
                "normal_alart INTEGER, " + //일반 알람 : 0, 1
                "PRIMARY KEY (medi_no, time_no)" +
                " );"
        //섭취 시간의 default 값 생각해보기
        //복용 여부의 default 값을 0으로 하는 것 고려해보기
        db.execSQL(query3)

        // alarm 번호 관리 table
        var query4 = "CREATE TABLE IF NOT EXISTS alarm_table ( " +
                "alarm_no INTEGER PRIMARY KEY, " + //시간 인덱스
                "time_no INTEGER " + //table1과 비교해서 사용
                " );"
        db.execSQL(query4)

        // alarm 기록 하는 table
        var query5 = "CREATE TABLE IF NOT EXISTS record_table ( " +
                "record_no INTEGER PRIMARY KEY, " + //시간 인덱스
                "alarm_no INTEGER, " + //alarm_talbe과 비교해서 사용
                "time_no INTEGER, " + //alarm_talbe과 비교해서 사용
                "set_date DATETIME, " + // 복용 해야하는 시간
                "check_date DATETIME DEFAULT NULL, " + // 실 복용 시간
                "is_last INTEGER DEFAULT 0" + // record의 마지막인가?
                " );"
        db.execSQL(query5)
    }

    fun putExample() {
        insertColumn_table1("영양제1", "1", "2021-03-17", "1", "1", "0", "1")
        insertColumn_table1("영양제2", "1", "2021-04-16", "1", "5", "0", "1")
        insertColumn_table1("영양제3", "1", "2021-05-22", "0", "21", "0", "1")
        insertColumn_table2("1", "1.25", "정", "2021-03-17 13:30:00", "2021-03-17 13:30:00", "0")
        insertColumn_table2("2", "1", "봉지", "2021-04-16 09:00:00", "2021-04-16 09:00:00", "0")
        insertColumn_table2("2", "2", "봉지", "2021-04-16 21:00:00", "2021-04-16 21:00:00", "0")
        insertColumn_table2("3", "1", "정", "2021-03-17 13:30:00", "2021-03-17 13:33:33", "0")
    }

    //데이터 추가
    fun insertColumn_table1(
        medi_name: String,
        set_cycle: String,
        start_date: String,
        re_type: String?,
        re_cycle: String?,
        call_alart: String,
        normal_alart: String
    ): Long {
        //(medi_no:Int, medi_name:String, set_cycle:Int, start_date:Date, re_type:Int, re_cycle:Int, call_alart:Int, normal_alart:Int)
        val values = ContentValues().apply {
            put("medi_name", medi_name)
            put("set_cycle", set_cycle)
            put("start_date", start_date)
            put("re_type", re_type)
            put("re_cycle", re_cycle)
            put("call_alart", call_alart)
            put("normal_alart", normal_alart)
        }
        return db.insert("table1", null, values)
    }

    fun insertColumn_table2(
        medi_no: String,
        set_amount: String,
        set_type: String,
        set_date: String,
        take_date: String,
        set_check: String
    ): Long {
        //(time_no:Int, medi_no:Int, set_amount:Double, set_type:Int, set_date:Datetime, take_date:Datetime, set_check:Int)
        val values = ContentValues().apply {
            put("medi_no", medi_no)
            put("set_amount", set_amount)
            put("set_type", set_type)
            put("set_date", set_date)
            put("take_date", take_date)
            put("set_check", set_check)
        }
        return db.insert("table2", null, values)
    }

    fun insertColumn_table3(
        medi_name: String,
        medi_no: String,
        time_no: String,
        set_cycle: String,
        start_date: String,
        re_type: String?,
        re_cycle: String?,
        set_amount: String,
        set_type: String,
        set_date: String,
        take_date: String,
        set_check: String,
        call_alart: String,
        normal_alart: String
    ) {
        //(time_no:Int, medi_no:Int, set_amount:Double, set_type:Int, set_date:Datetime, take_date:Datetime, set_check:Int)
        var query =
            "INSERT INTO table3 (medi_name, medi_no, set_cycle, start_date, re_type, re_cycle, set_amount, set_type, set_date, take_date, set_check, call_alart, normal_alart) " +
                    "VALUES (\"${medi_name}\", ${medi_no}, ${set_cycle}, \"${start_date}\", ${re_type}, ${re_cycle}, ${set_amount}, \"${set_type}\", \"${set_date}\", \"${take_date}\", ${set_check}, ${call_alart}, ${normal_alart} );"
        db.execSQL(query)
    }



    fun get_noticeinfo2_all(): ArrayList<NoticeInfo> {
        val noticeinfo2_list = ArrayList<NoticeInfo>()
        val query = "SELECT * FROM table1"
        val tb1_cursor = db.rawQuery(query, null)
        tb1_cursor.moveToFirst()
        do {
            // medi_no 에 해당하는 모든 table2 데이터 가져오기
            val time_list = ArrayList<NoticeAlarmInfo>()
            val query2 =
                "SELECT * FROM table2 WHERE medi_no=${tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no"))}"
            val tb2_cursor = db.rawQuery(query2, null)
            tb2_cursor.moveToFirst()
            do {
                time_list.add(
                    NoticeAlarmInfo(
                        time_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("time_no")),
                        medi_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("medi_no")),
                        set_amount = tb2_cursor.getDouble(tb2_cursor.getColumnIndex("set_amount")),
                        set_type = tb2_cursor.getString(tb2_cursor.getColumnIndex("set_type")),
                        set_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("set_date")),
                        take_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("take_date")),
                        set_check = tb2_cursor.getInt(tb2_cursor.getColumnIndex("set_check"))
                    )
                )
            } while (tb2_cursor.moveToNext())

            noticeinfo2_list.add(
                NoticeInfo(
                    medi_no = tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no")),
                    medi_name = tb1_cursor.getString(tb1_cursor.getColumnIndex("medi_name")),
                    set_cycle = tb1_cursor.getInt(tb1_cursor.getColumnIndex("set_cycle")),
                    start_date = tb1_cursor.getString(tb1_cursor.getColumnIndex("start_date")),
                    re_type = tb1_cursor.getInt(tb1_cursor.getColumnIndex("re_type")),
                    re_cycle = tb1_cursor.getInt(tb1_cursor.getColumnIndex("re_cycle")),
                    call_alart = tb1_cursor.getInt(tb1_cursor.getColumnIndex("call_alart")),
                    normal_alart = tb1_cursor.getInt(tb1_cursor.getColumnIndex("normal_alart")),
                    time_list = time_list

                )
            )
        } while (tb1_cursor.moveToNext())
        return noticeinfo2_list
    }

    //테이블1,2 join&Select
    fun JoinAndSelectAll(): Cursor {
        var query = "SELECT * FROM table1 INNER JOIN table2 on table1.medi_no = table2.medi_no"
        return db.rawQuery(query, null)
    }

    // '오늘'화면에 필요한 데이터들을 가져오기_ver1
    fun get_TodayInfo_all(): ArrayList<TodayInfo> {
        val todayInfoList = ArrayList<TodayInfo>()
        val query = "SELECT * FROM table1 INNER JOIN table2 on table1.medi_no = table2.medi_no"
        val cursor = db.rawQuery(query, null)
        //cursor.moveToFirst()
        while(cursor.moveToNext())
        {
            todayInfoList.add(
                TodayInfo(
                    medi_no = cursor.getInt(cursor.getColumnIndex("medi_no")),
                    time_no = cursor.getInt(cursor.getColumnIndex("time_no")),
                    medi_name = cursor.getString(cursor.getColumnIndex("medi_name")),
                    set_date = cursor.getString(cursor.getColumnIndex("set_date")),
                    take_date = cursor.getString(cursor.getColumnIndex("take_date")),
                    set_check = cursor.getInt(cursor.getColumnIndex("set_check"))
                )
            )

        }
        return todayInfoList
    }

    //'오늘'화면에 필요한 데이터들을 가져오기_ver2
    /*fun get_TodayInfo_all2(): ArrayList<TodayInfo_ver2> {
        val todayInfoList = ArrayList<TodayInfo_ver2>()
        val query = "SELECT * FROM table1"
        val tb1_cursor = db.rawQuery(query, null)
        tb1_cursor.moveToFirst()

        do {
            // medi_no 에 해당하는 모든 table2 데이터 가져오기
            val todayTimeList = ArrayList<TodayTimeInfo>()
            val query2 =
                "SELECT * FROM table2 WHERE medi_no=${tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no"))}"
            val tb2_cursor = db.rawQuery(query2, null)
            tb2_cursor.moveToFirst()
            do {
                todayTimeList.add(
                    TodayTimeInfo(
                        medi_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("medi_no")),
                        time_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("time_no")),
                        set_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("set_date")),
                        take_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("take_date")),
                        set_check = tb2_cursor.getInt(tb2_cursor.getColumnIndex("set_check"))
                    )
                )
            } while (tb2_cursor.moveToNext())

            todayInfoList.add(
                TodayInfo_ver2(
                    medi_no = tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no")),
                    medi_name = tb1_cursor.getString(tb1_cursor.getColumnIndex("medi_name")),
                    time_list = todayTimeList
                )
            )
        } while (tb1_cursor.moveToNext())
        return todayInfoList
    }*/

    // '관리'화면에 필요한 데이터들을 가져오기
    fun get_ManageInfo_all(): ArrayList<ManageInfo> {
        val ManageInfoList = ArrayList<ManageInfo>()
        val query = "SELECT * FROM table1"
        val tb1_cursor = db.rawQuery(query, null)
        tb1_cursor.moveToFirst()

        do {
            // medi_no 에 해당하는 모든 table2 데이터 가져오기
            val ManageTimeList = ArrayList<ManageTimeInfo>()
            val query2 =
                "SELECT * FROM table2 WHERE medi_no=${tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no"))}"
            val tb2_cursor = db.rawQuery(query2, null)
            tb2_cursor.moveToFirst()
            do {
                ManageTimeList.add(
                    ManageTimeInfo(
                        medi_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("medi_no")),
                        time_no = tb2_cursor.getInt(tb2_cursor.getColumnIndex("time_no")),
                        set_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("set_date")),
                        take_date = tb2_cursor.getString(tb2_cursor.getColumnIndex("take_date")),
                        set_check = tb2_cursor.getInt(tb2_cursor.getColumnIndex("set_check"))
                    )
                )
            } while (tb2_cursor.moveToNext())

            ManageInfoList.add(
                ManageInfo(
                    medi_no = tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no")),
                    medi_name = tb1_cursor.getString(tb1_cursor.getColumnIndex("medi_name")),
                    time_list = ManageTimeList
                )
            )
        } while (tb1_cursor.moveToNext())
        return ManageInfoList
    }


    //데이터 검색
    fun selectColumn(mytable: String, select: String, condition: String): Cursor {
        //select : 찾고자 하는 정보 ex) "id" 또는 "id, mediname" 의 형식으로 작성
        //condition : 찾고자 하는 조건 ex) "id='1'" 또는 "id='1' AND mediname='비타민'" 의 형식으로 작성
        var query = "SELECT " + select + " FROM " + mytable + " WHERE " + condition
        return db.rawQuery(query, null)
    }

    //데이터 전체 출력
    fun selectAllColumn(mytable: String): Cursor {
        var query = "SELECT * FROM " + mytable
        return db.rawQuery(query, null)
    }

    //데이터 수정
    fun updateColumn(mytable: String, update: String, condition: String) {
        //update : 수정하고자 하는 정보 ex) "mediname='비타민'" 의 형식으로 작성
        //condition : 수정하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "UPDATE " + mytable + " SET " + update + " WHERE " + condition
        db.execSQL(query)
    }

    //데이터 삭제
    fun deleteColumn(mytable: String, condition: String) {
        //condition : 삭제하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "DELETE FROM " + mytable + " WHERE " + condition
        db.execSQL(query)
    }

    //테이블의 모든 데이터 삭제
    fun deleteAllColumns_table(mytable: String) {
        var query = "DELETE FROM " + mytable
        db.execSQL(query)
    }

    //DB의 모든 데이터 삭제
    fun deleteAllColumns_DB() {
        var query = "DELETE FROM mediDB"
        db.execSQL(query)
    }

    // medi_no 에 해당하는 alarm 모두 제거
    fun delete_alarm_all_by_medi_no(no: Int) {
        deleteColumn("table2", "medi_no=${no}")
    }

    // medi_no 에 해당하는 alarm의 개수 반환
    fun get_alarm_cnt_by_medi_no(no: Int): Int {
        var query = "SELECT * FROM table2 WHERE medi_no = ${no}"
        val cursor = db.rawQuery(query, null)
        return cursor.count
    }

    // table1의 전화알람 정보 업데이트
    fun set_call_state(medi_no: Int, flag: Boolean) {
        if (flag)
            updateColumn("table1", "call_alart=1", "medi_no=${medi_no}")
        else
            updateColumn("table1", "call_alart=0", "medi_no=${medi_no}")
    }

    // table1의 문자알람 정보 업데이트
    fun set_normal_state(medi_no: Int, flag: Boolean) {
        if (flag)
            updateColumn("table1", "normal_alart=1", "medi_no=${medi_no}")
        else
            updateColumn("table1", "normal_alart=0", "medi_no=${medi_no}")
    }

    fun delete_medicine_by_id(medi_no: Int) {
        deleteColumn("table1", "medi_no=${medi_no}")
        deleteColumn("table2", "medi_no=${medi_no}")
    }

    fun insert_alarm(time_no: Int): Long {
        // alarm_table time_no 에 대한 insert 쿼리 문
        val values = ContentValues().apply {
            put("time_no", time_no)
        }
        return db.insert("alarm_table", null, values)
    }

    fun get_alarm_no_by_time_no(time_no: Int): ArrayList<Int> {
        // medi_no 에 해당하는 모든 time_no 가져오기
        val alarm_no_list = ArrayList<Int>()

        var query_get_alarm_no = "select * from alarm_table where time_no=${time_no}"
        val cursor = db.rawQuery(query_get_alarm_no, null)
        cursor.moveToFirst()
        do {
            alarm_no_list.add(cursor.getInt(cursor.getColumnIndex("alarm_no")))
        } while (cursor.moveToNext())

        return alarm_no_list
    }

    fun get_time_no_by_medi_no(medi_no: Int): ArrayList<Int> {
        // medi_no 에 해당하는 모든 time_no 가져오기
        val time_no_list = ArrayList<Int>()

        var query_get_time_no = "select * from table2 where medi_no=${medi_no}"
        val cursor = db.rawQuery(query_get_time_no, null)
        cursor.moveToFirst()
        do {
            time_no_list.add(cursor.getInt(cursor.getColumnIndex("time_no")))
        } while (cursor.moveToNext())

        return time_no_list
    }

    fun delete_alarm_by_alarm_no(alarm_no: Int) {
        // alarm_table 해당하는거 지우기
        var query = "DELETE FROM alarm_table WHERE alarm_no=$alarm_no"
        db.execSQL(query)
    }

    fun get_noticeAlarmInfo_by_alarm_no(alarm_no: Int): NoticeAlarmInfo {
        // alarm_no 에 해당하는 NoticeAlarmInfo 반환
        var query_get_time_no = "select * from alarm_table where alarm_no=${alarm_no}"
        val cursor = db.rawQuery(query_get_time_no, null)
        cursor.moveToFirst()
        val time_no = cursor.getInt(cursor.getColumnIndex("time_no"))
        val query_get_alarm_info = "select * from table2 where time_no=${time_no}"
        val cursor2 = db.rawQuery(query_get_alarm_info, null)
        cursor2.moveToFirst()
        return NoticeAlarmInfo(
            time_no = cursor2.getInt(cursor2.getColumnIndex("time_no")),
            medi_no = cursor2.getInt(cursor2.getColumnIndex("medi_no")),
            set_amount = cursor2.getDouble(cursor2.getColumnIndex("set_amount")),
            set_type = cursor2.getString(cursor2.getColumnIndex("set_type")),
            set_date = cursor2.getString(cursor2.getColumnIndex("set_date")),
            take_date = cursor2.getString(cursor2.getColumnIndex("take_date")),
            set_check = cursor2.getInt(cursor2.getColumnIndex("set_check"))
        )
    }

    fun get_noticeInfo_by_medi_no(medi_no: Int): NoticeInfo {
        var query_get_noticeInfo = "select * from table1 where medi_no=${medi_no}"
        val cursor = db.rawQuery(query_get_noticeInfo, null)
        cursor.moveToFirst()
        return NoticeInfo(
            medi_no = cursor.getInt(cursor.getColumnIndex("medi_no")),
            medi_name = cursor.getString(cursor.getColumnIndex("medi_name")),
            set_cycle = cursor.getInt(cursor.getColumnIndex("set_cycle")),
            start_date = cursor.getString(cursor.getColumnIndex("start_date")),
            re_type = cursor.getInt(cursor.getColumnIndex("re_type")),
            re_cycle = cursor.getInt(cursor.getColumnIndex("re_cycle")),
            call_alart = cursor.getInt(cursor.getColumnIndex("call_alart")),
            normal_alart = cursor.getInt(cursor.getColumnIndex("normal_alart"))
        )
    }

    fun insertRecord(alarm_no: Int, time_no: Int, alarm_datetime: String): Long {
        val date_alarm_split = alarm_datetime.split(" ")[0].split("-")
        val time_alarm_split = alarm_datetime.split(" ")[1].split(":")
        val alarm_date_time_2 = "${date_alarm_split[0].toInt()}-${"%02d".format(date_alarm_split[1].toInt())}-${"%02d".format(date_alarm_split[2].toInt())} " +
                "${"%02d".format(time_alarm_split[0].toInt())}:${"%02d".format(time_alarm_split[1].toInt())}:${"%02d".format(time_alarm_split[2].toInt())}"

        // record_table 에 대한 insert 쿼리 문
        val values = ContentValues().apply {
            put("alarm_no", alarm_no)
            put("time_no", time_no)
            put("set_date", alarm_date_time_2)
        }
        return db.insert("record_table", null, values)
    }

    fun insertRecordNextWeek(
        alarm_no: Int,
        time_no: Int,
        alarm_datetime: String,
        set_cycle: Int,
        re_type: Int,
        re_cycle: Int
    ){
        val date_alarm_split = alarm_datetime.split(" ")[0].split("-")
        val time_alarm_split = alarm_datetime.split(" ")[1].split(":")
        val calendar = Calendar.getInstance()
        calendar.set(
            date_alarm_split[0].toInt(), // year
            date_alarm_split[1].toInt()-1, // month
            date_alarm_split[2].toInt(), // date
            time_alarm_split[0].toInt(), // hour
            time_alarm_split[1].toInt(), // minute
            time_alarm_split[2].toInt()  // second
        )

        // 다음주의 첫번째 날로 이동
        if(set_cycle==0){
            // 매일
            calendar.add(Calendar.DATE, 1)
        } else if(re_type==0){
            // 요일 반복
            val dow_arrayList = DowConverterFactory.convert_int_to_arrayList(re_cycle)
            for (i in 0..6) {
                calendar.add(Calendar.DATE, 1)
                if (dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK) - 1]) {
                    break;
                }
            }
        } else if(re_type==1){
            // N일 반복
            calendar.add(Calendar.DATE, re_cycle)
        } else if(re_type==2){
            // 월 반복
            // 은 사용하지 않음
        }
        insertRecordWeek(
            alarm_no,
            time_no,
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(Calendar.DATE)} "+
                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)} ",
            set_cycle,
            re_type,
            re_cycle
        )
    }

    fun insertRecordWeek(
        alarm_no: Int,
        time_no: Int,
        alarm_datetime: String,
        set_cycle: Int,
        re_type: Int,
        re_cycle: Int
    ) {
        // alarm_datetime 이 있는 주의 알람 모두 설정하기
        val start_date_split = alarm_datetime.split(" ")[0].split("-")
        val start_time_split = alarm_datetime.split(" ")[1].split(":")
        var calendar = Calendar.getInstance()
        calendar.set(
            start_date_split[0].toInt(), // year
            start_date_split[1].toInt() - 1, // month
            start_date_split[2].toInt(), // date
            start_time_split[0].toInt(), // hour
            start_time_split[1].toInt(), // minute
            start_time_split[2].toInt()  // second
        )

        // 만약에 alarm_datetime이.. 오늘 이전이면 오늘 이후까지 이동
        val today_calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if(set_cycle==0){
            // 매일
            while(calendar < today_calendar){
                calendar.add(Calendar.DATE, 1)
            }
        } else if(re_type==0){
            // 요일반복 (하루씩 늘림 + 현재 요일이 해당되어야함)
            val dow_arrayList = DowConverterFactory.convert_int_to_arrayList(re_cycle!!)
            while(calendar < today_calendar && dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK)-1]){
                calendar.add(Calendar.DATE, 1)
            }
        } else if(re_type==1){
            // N일 반복
            while(calendar < today_calendar){
                calendar.add(Calendar.DATE, re_cycle)
            }
        } else if(re_type==2){
            // N개월 반복
            while(calendar < today_calendar){
                calendar.add(Calendar.MONTH, re_cycle)
            }
        }

        // calendar 가 포함된 주의 일요일 찾기
        var calendar_end = calendar.clone() as Calendar // alarm_date가 있는 주의 일요일
        while (calendar_end.get(Calendar.DAY_OF_WEEK) != 1) {
            calendar_end.add(Calendar.DATE, 1)
        }

        var last_record_id = 0L // 새로 들어가는 record 중 마지막 record_no
        if (set_cycle==0) {
            // 매일 반복
            while(calendar_end >= calendar){
                last_record_id = insertRecord(alarm_no, time_no, "${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH)+1}-" +
                        "${calendar.get(Calendar.DATE)} " +
                        "${calendar.get(Calendar.HOUR_OF_DAY)}:" +
                        "${calendar.get(Calendar.MINUTE)}:" +
                        "${calendar.get(Calendar.SECOND)}"
                )
                calendar.add(Calendar.DATE, 1)
            }
        } else if (re_type == 0) {
            // 요일 반복
            val dow_arrayList = DowConverterFactory.convert_int_to_arrayList(re_cycle!!)
            while(calendar_end.compareTo(calendar) != -1){
                if(dow_arrayList[calendar.get(Calendar.DAY_OF_WEEK)-1]) {
                    last_record_id = insertRecord(alarm_no, time_no, "${calendar.get(Calendar.YEAR)}-" +
                            "${calendar.get(Calendar.MONTH)+1}-" +
                            "${calendar.get(Calendar.DATE)} " +
                            "${calendar.get(Calendar.HOUR_OF_DAY)}:" +
                            "${calendar.get(Calendar.MINUTE)}:" +
                            "${calendar.get(Calendar.SECOND)}"
                    )
                }
                calendar.add(Calendar.DATE, 1)
            }
        } else if (re_type == 1) {
            // 일 반복
            while(calendar_end.compareTo(calendar) != -1){
                last_record_id = insertRecord(alarm_no, time_no, "${calendar.get(Calendar.YEAR)}-" +
                        "${calendar.get(Calendar.MONTH)+1}-" +
                        "${calendar.get(Calendar.DATE)} " +
                        "${calendar.get(Calendar.HOUR_OF_DAY)}:" +
                        "${calendar.get(Calendar.MINUTE)}:" +
                        "${calendar.get(Calendar.SECOND)}"
                )
                calendar.add(Calendar.DATE, re_cycle!!)
            }
        } else if(re_type==2){
            // 월 반복은 setAlarmRepeat 가 아니기 때문에 지정하지 않음
        }
        set_record_is_last(last_record_id.toInt(), 1) // 마지막 값은 1로 설정
    }

    fun set_record_is_last(record_no:Int, value:Int){
        var query = "UPDATE record_table SET is_last=${value} WHERE record_no=${record_no}"
        db.execSQL(query)
    }

    fun get_record_is_last(record_no:Int):Boolean{
        var query = "SELECT * FROM record_table WHERE record_no=${record_no}"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        val is_last = cursor.getInt(cursor.getColumnIndex("is_last"))
        return is_last==1
    }

    fun get_record_no(alarm_no: Int):Int{
        // Todo : 여기서 set_date와 현재 시간을 비교하여, 너무 많이 지났으면 -1을 출력하게 하는 방법 있음
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var query = "SELECT * FROM record_table WHERE set_date<=\"${simpleDateFormat.format(Date())}\" ORDER BY set_date desc"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        return cursor.getInt(cursor.getColumnIndex("record_no"))
    }

    fun get_record_set_datetime(record_no: Int):String{
        var query = "SELECT * FROM record_table WHERE record_no=${record_no}"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("set_date"))
    }

    fun set_record_check(record_no: Int){
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var query = "SELECT * FROM record_table WHERE record_no=${record_no}"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        if(cursor.isNull(cursor.getColumnIndex("check_date"))){
            var query_update =
                "UPDATE record_table SET check_date=\"${simpleDateFormat.format(Date())}\" WHERE record_no=${record_no}"
            db.execSQL(query_update)
        }
    }

    fun delete_record_by_alarm_no(alarm_no: Int) {
        // alarm_table 해당하는거 지우기
        var query = "DELETE FROM record_table WHERE alarm_no=$alarm_no"
        db.execSQL(query)
    }
}