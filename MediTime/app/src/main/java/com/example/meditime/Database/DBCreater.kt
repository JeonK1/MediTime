package com.example.meditime.Database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.meditime.Model.NoticeInfo
import com.example.meditime.Model.NoticeAlarmInfo

class DBCreater(dbHelper: DBHelper, private val db: SQLiteDatabase){

    fun createTable(){
        //table1 생성
        var query1 = "CREATE TABLE IF NOT EXISTS table1 ( " +
                "medi_no INTEGER PRIMARY KEY, "+ //약 인덱스
                "medi_name CHAR(50), "+ //약 이름
                "set_cycle INTEGER, "+ //복용 주기 : 0, 1
                "start_date DATE, "+ //시작 날짜 : yy-mm-dd
                "re_type INTEGER, "+ //반복 타입 : 0(요일), 1(일), 2(개월)
                "re_cycle INTEGER, "+ //반복 주기 : 2진수 변환
                "call_alart INTEGER, "+ //전화 알람 : 0, 1
                "normal_alart INTEGER "+ //일반 알람 : 0, 1
                //복용 주기가 0일 경우 반복 타입과 반복 주기가 null 값이 되게 설정해야 함
                //전화 알람 default 값을 0, 일반 알람 default 값을 1로 하는 것 고려해보기
                " );"
        db.execSQL(query1)

        //table2 생성
        var query2 = "CREATE TABLE IF NOT EXISTS table2 ( " +
                "time_no INTEGER PRIMARY KEY, "+ //시간 인덱스
                "medi_no INTEGER, "+ //table1과 비교해서 사용
                "set_amount DOUBLE, "+ //복용량
                "set_type CHAR(50), "+ //복용 타입 : 정, 봉지, ...
                "set_date DATETIME, "+ //복용 시간 : yy-mm-dd hh:mm:ss
                "take_date DATETIME, "+ //섭취 시간 : yy-mm-dd hh:mm:ss
                "set_check INTEGER "+ //복용 여부 : 0, 1
                " );"
        //섭취 시간의 default 값 생각해보기
        //복용 여부의 default 값을 0으로 하는 것 고려해보기
        db.execSQL(query2)

        //table3 생성
        var query3 = "CREATE TABLE IF NOT EXISTS table3 ( " +
                "medi_name CHAR(50), "+ //약 이름
                "medi_no INTEGER, "+ //table1과 비교해서 사용
                "time_no INTEGER, "+ //시간 인덱스
                "set_cycle INTEGER, "+ //복용 주기 : 0, 1
                "start_date DATE, "+ //시작 날짜 : yy-mm-dd
                "re_type INTEGER, "+ //반복 타입 : 0(요일), 1(일), 2(개월)
                "re_cycle INTEGER, "+ //반복 주기 : 2진수 변환
                "set_amount DOUBLE, "+ //복용량
                "set_type CHAR(50), "+ //복용 타입 : 정, 봉지, ...
                "set_date DATETIME, "+ //복용 시간 : yy-mm-dd hh:mm:ss
                "take_date DATETIME, "+ //섭취 시간 : yy-mm-dd hh:mm:ss
                "set_check INTEGER, "+ //복용 여부 : 0, 1
                "call_alart INTEGER, "+ //전화 알람 : 0, 1
                "normal_alart INTEGER, "+ //일반 알람 : 0, 1
                "PRIMARY KEY (medi_no, time_no)"+
                " );"
        //섭취 시간의 default 값 생각해보기
        //복용 여부의 default 값을 0으로 하는 것 고려해보기
        db.execSQL(query3)

        // alarm 번호 관리 table
        var query4 = "CREATE TABLE IF NOT EXISTS alarm_table ( " +
                "alarm_no INTEGER PRIMARY KEY, "+ //시간 인덱스
                "time_no INTEGER, "+ //table1과 비교해서 사용
                "set_check INTEGER " +
                " );"
        db.execSQL(query4)
    }

    fun putExample(){
        insertColumn_table1("영양제1", "1", "2021-03-17", "1", "1", "0", "1")
        insertColumn_table1( "영양제2", "1", "2021-04-16", "1", "5", "0", "1")
        insertColumn_table1( "영양제3", "1", "2021-05-22", "0", "21", "0", "1")
        insertColumn_table2( "1", "1.25", "정", "2021-03-17 13:30:00", "2021-03-17 13:30:00", "0")
        insertColumn_table2( "2", "1", "봉지", "2021-04-16 09:00:00", "2021-04-16 09:00:00", "0")
        insertColumn_table2( "2", "2", "봉지", "2021-04-16 21:00:00", "2021-04-16 21:00:00", "0")
        insertColumn_table2( "3", "1", "정", "2021-03-17 13:30:00", "2021-03-17 13:33:33", "0")
        //insertColumn_table3( "영양제1", "1", "1", "1", "2021-03-17","1", "5","1","정", "2021-03-17 09:30:00", "2021-03-17 13:33:33", "1", "0","1")
        //insertColumn_table3( "영양제1",  "1", "2","1", "2021-03-17","1", "5","1","정", "2021-03-17 13:30:00", "2021-03-17 13:33:33", "1", "0","1")
        //insertColumn_table3( "영양제2", "2", "1", "0", "2021-04-16","1", "1","1","정", "2021-04-16 13:30:00", "2021-04-16 13:33:33", "1", "0","1")
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

    fun insertColumn_table3(medi_name:String, medi_no:String, time_no:String, set_cycle:String, start_date:String, re_type:String?, re_cycle:String?, set_amount:String, set_type:String, set_date:String, take_date:String, set_check:String, call_alart:String, normal_alart:String){
        //(time_no:Int, medi_no:Int, set_amount:Double, set_type:Int, set_date:Datetime, take_date:Datetime, set_check:Int)
        var query = "INSERT INTO table3 (medi_name, medi_no, set_cycle, start_date, re_type, re_cycle, set_amount, set_type, set_date, take_date, set_check, call_alart, normal_alart) " +
                "VALUES (\"${medi_name}\", ${medi_no}, ${set_cycle}, \"${start_date}\", ${re_type}, ${re_cycle}, ${set_amount}, \"${set_type}\", \"${set_date}\", \"${take_date}\", ${set_check}, ${call_alart}, ${normal_alart} );"
        db.execSQL(query)
    }

    //테이블1,2 join&Select
    fun JoinAndSelectAll(): Cursor{
        var query = "SELECT * FROM table1 INNER JOIN table2 on table1.medi_no = table2.medi_no"
        return db.rawQuery(query, null)
    }

    fun get_noticeinfo2_all(): ArrayList<NoticeInfo>{
        val noticeinfo2_list = ArrayList<NoticeInfo>()
        val query = "SELECT * FROM table1"
        val tb1_cursor = db.rawQuery(query, null)
        tb1_cursor.moveToFirst()
        do {
            // medi_no 에 해당하는 모든 table2 데이터 가져오기
            val time_list = ArrayList<NoticeAlarmInfo>()
            val query2 = "SELECT * FROM table2 WHERE medi_no=${tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no"))}"
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
                ))
            }while (tb2_cursor.moveToNext())
            noticeinfo2_list.add(
                NoticeInfo(
                    medi_no = tb1_cursor.getInt(tb1_cursor.getColumnIndex("medi_no")),
                    medi_name = tb1_cursor.getString(tb1_cursor.getColumnIndex("medi_name")),
                    set_cycle = tb1_cursor.getInt(tb1_cursor.getColumnIndex("set_cycle")),
                    start_date = tb1_cursor.getString(tb1_cursor.getColumnIndex("start_date")),
                    re_type = tb1_cursor.getInt(tb1_cursor.getColumnIndex("re_type")),
                    re_cycle = tb1_cursor.getInt(tb1_cursor.getColumnIndex("re_cycle")),
                    call_alart = tb1_cursor.getInt(tb1_cursor.getColumnIndex("call_alart")),
                    normal_alart =  tb1_cursor.getInt(tb1_cursor.getColumnIndex("normal_alart")),
                    time_list = time_list

            ))
        }while (tb1_cursor.moveToNext())
        return noticeinfo2_list
    }

    //데이터 검색
    fun selectColumn(mytable: String, select:String, condition:String): Cursor {
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
    fun updateColumn(mytable: String, update:String, condition:String){
        //update : 수정하고자 하는 정보 ex) "mediname='비타민'" 의 형식으로 작성
        //condition : 수정하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "UPDATE " + mytable + " SET " + update + " WHERE " + condition
        db.execSQL(query)
    }

    //데이터 삭제
    fun deleteColumn(mytable: String, condition:String){
        //condition : 삭제하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "DELETE FROM " + mytable + " WHERE " + condition
        db.execSQL(query)
    }

    //테이블의 모든 데이터 삭제
    fun deleteAllColumns_table(mytable: String){
        var query = "DELETE FROM " + mytable
        db.execSQL(query)
    }

    //DB의 모든 데이터 삭제
    fun deleteAllColumns_DB(){
        var query = "DELETE FROM mediDB"
        db.execSQL(query)
    }

    // medi_no 에 해당하는 alarm 모두 제거
    fun delete_alarm_all_by_medi_no(no: Int){
        deleteColumn("table2", "medi_no=${no}")
    }

    // medi_no 에 해당하는 alarm의 개수 반환
    fun get_alarm_cnt_by_medi_no(no: Int): Int {
        var query = "SELECT * FROM table2 WHERE medi_no = ${no}"
        val cursor =  db.rawQuery(query, null)
        return cursor.count
    }

    // table1의 전화알람 정보 업데이트
    fun set_call_state(medi_no:Int, flag:Boolean) {
        if(flag)
            updateColumn("table1","call_alart=1", "medi_no=${medi_no}")
        else
            updateColumn("table1","call_alart=0", "medi_no=${medi_no}")
    }

    // table1의 문자알람 정보 업데이트
    fun set_normal_state(medi_no:Int, flag:Boolean) {
        if(flag)
            updateColumn("table1","normal_alart=1", "medi_no=${medi_no}")
        else
            updateColumn("table1","normal_alart=0", "medi_no=${medi_no}")
    }

    fun delete_medicine_by_id(medi_no: Int){
        deleteColumn("table1", "medi_no=${medi_no}")
        deleteColumn("table2", "medi_no=${medi_no}")
    }

    fun insert_alarm(time_no: Int): Long {
        // alarm_table time_no 에 대한 insert 쿼리 문
        val values = ContentValues().apply {
            put("time_no", time_no)
            put("set_check", 0)
        }
        return db.insert("alarm_table", null, values)
    }

    fun set_check_alarm(alarm_no:Int){
        // alarm_table set_check 에 1로 설정하기
        var query = "UPDATE alarm_table SET set_check=1 WHERE alarm_no=$alarm_no"
        db.execSQL(query)
    }

    fun get_alarm_no_by_time_no(time_no: Int): ArrayList<Int> {
        // medi_no 에 해당하는 모든 time_no 가져오기
        val alarm_no_list = ArrayList<Int>()

        var query_get_alarm_no = "select * from alarm_table where time_no=${time_no}"
        val cursor = db.rawQuery(query_get_alarm_no, null)
        cursor.moveToFirst()
        do {
            alarm_no_list.add(cursor.getInt(cursor.getColumnIndex("alarm_no")))
        }while (cursor.moveToNext())

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
        }while (cursor.moveToNext())

        return time_no_list
    }

    fun set_delete_alarm_by_alarm_no(alarm_no: Int){
        // alarm_table 해당하는거 지우기
        var query = "DELETE FROM alarm_table WHERE alarm_no=$alarm_no"
        db.execSQL(query)
    }



}