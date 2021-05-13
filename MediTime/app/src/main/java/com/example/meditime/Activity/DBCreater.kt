package com.example.meditime.Activity

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

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
    }

    fun putExample(){
        insertColumn_table1("영양제1", "1", "'2021-03-17'", "1", "1", "0", "1")
        insertColumn_table1( "영양제2", "1", "'2021-04-16'", "1", "5", "0", "1")
        insertColumn_table1( "영양제3", "1", "'2021-05-22'", "2", "21", "0", "1")
        insertColumn_table1( "영양제4", "1", "'2021-03-15'", "1", "14", "0", "1")
        insertColumn_table1( "영양제5", "1", "'2021-03-03'", "0", "65", "0", "1")
        insertColumn_table2( "1", "1.25", "정", "'2021-03-17 13:30:00'", "'2021-03-17 13:30:00'", "0")
        insertColumn_table2( "2", "1", "봉지", "'2021-04-16 09:00:00'", "'2021-04-16 09:00:00'", "0")
        insertColumn_table2( "2", "2", "봉지", "'2021-04-16 21:00:00'", "'2021-04-16 21:00:00'", "0")
    }

    //데이터 추가
    fun insertColumn_table1(medi_name:String, set_cycle:String, start_date:String, re_type:String, re_cycle:String, call_alart:String, normal_alart:String){
        //(medi_no:Int, medi_name:String, set_cycle:Int, start_date:Date, re_type:Int, re_cycle:Int, call_alart:Int, normal_alart:Int)
        var query = "INSERT INTO table1 (medi_name, set_cycle, start_date, re_type, re_cycle, call_alart, normal_alart) " +
                "VALUES ( \"${medi_name}\", ${set_cycle}, ${start_date}, ${re_type}, ${re_cycle}, ${call_alart}, ${normal_alart} );"
        db.execSQL(query)
    }

    fun insertColumn_table2(medi_no:String, set_amount:String, set_type:String, set_date:String, take_date:String, set_check:String){
        //(time_no:Int, medi_no:Int, set_amount:Double, set_type:Int, set_date:Datetime, take_date:Datetime, set_check:Int)
        var query = "INSERT INTO table2 (medi_no, set_amount, set_type, set_date, take_date, set_check) " +
                "VALUES (${medi_no}, ${set_amount}, \"${set_type}\", ${set_date}, ${take_date}, ${set_check} );"
        db.execSQL(query)
    }

    //데이터 검색
    fun selectColumn(mytable: String, select:String, condition:String): Cursor? {
        //select : 찾고자 하는 정보 ex) "id" 또는 "id, mediname" 의 형식으로 작성
        //condition : 찾고자 하는 조건 ex) "id='1'" 또는 "id='1' AND mediname='비타민'" 의 형식으로 작성
        var query = "SELECT " + select + " FROM " + mytable + " WHERE " + condition
        return db.rawQuery(query, null)
    }

    //데이터 전체 출력
    fun selectAllColumn(mytable: String): Cursor? {
        var query = "SELECT * FROM " + mytable
        return db.rawQuery(query, null)
    }

    //데이터 수정
    fun updateColumn(mytable: String, update:String, condition:String){
        //update : 수정하고자 하는 정보 ex) "mediname='비타민'" 의 형식으로 작성
        //condition : 수정하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "UPDATE " + mytable + " SET " + update + " WHERE id = " + condition
        db.execSQL(query)
    }

    //데이터 삭제
    fun deleteColumn(mytable: String, condition:String){
        //condition : 삭제하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "DELETE FROM " + mytable + " WHERE id = " + condition
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

}