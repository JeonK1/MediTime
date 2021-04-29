package com.example.meditime.Activity

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DBCreater(cont: Context){

    lateinit var dbHelper : DBHelper
    lateinit var database : SQLiteDatabase

    //테이블 생성
    fun createTable(){
        dbHelper = DBHelper(cont, "MediTime.db", null, 1)
        database = dbHelper.writableDatabase
    }

    //테이블 삭제
    fun deleteTable(oldVer:Int, newVer:Int){
        dbHelper.onUpgrade(database, oldVer, newVer)
    }

    //데이터 추가
    fun insertColumn(id:Int, mediname:String, medialart:Long, meditake:Long, call:Int, medicycle:Int){
        var query = "INSERT INTO MediTime " + "( " +
                "'id' VALUES" + id +
                "'mediname' VALUES" + mediname +
                "'medialart' VALUES" + medialart +
                "'meditake' VALUES" + meditake +
                "'call' VALUES" + call +
                "'medicycle' VALUES" + medicycle +
                " );"
        database.execSQL(query)
    }

    //데이터 검색
    fun selectColumn(select:String, condition:String){
        //select : 찾고자 하는 정보 ex) "id" 또는 "id, mediname" 의 형식으로 작성
        //condition : 찾고자 하는 조건 ex) "id='1'" 또는 "id='1' AND mediname='비타민'" 의 형식으로 작성
        var query = "SELECT " + select + " FROM MediTime WHERE " + condition
        database.execSQL(query)
    }

    //데이터 전체 출력
    fun selectAllColumn(){
        var query = "SELECT * FROM MediTime"
        database.execSQL(query)
    }

    //데이터 수정
    fun updateColumn(update:String, condition:String){
        //update : 수정하고자 하는 정보 ex) "mediname='비타민'" 의 형식으로 작성
        //condition : 수정하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "UPDATE MediTime SET " + update + " WHERE id = " + condition
        database.execSQL(query)
    }

    //데이터 삭제
    fun deleteColumn(condition:String){
        //condition : 삭제하고자 하는 column의 id ex) "3" 의 형식으로 작성
        // **정수형이 아님**
        // **문자열로 작성**
        var query = "DELETE FROM MediTime WHERE id = " + condition
        database.execSQL(query)
    }

    //모든 데이터 삭제
    fun deleteAllColumns(){
        var query = "DELETE FROM MediTime"
        database.execSQL(query)
    }

}