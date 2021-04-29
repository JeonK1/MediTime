package com.example.meditime.Activity

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    var mytable : String? = name

    //테이블 생성
    override fun onCreate(db: SQLiteDatabase) {
        var sql : String = "CREATE TABLE " + mytable + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," + // id 인덱스 integer(100)
                "mediname VARCHAR(50)" + // mediname 약 이름 varchar(50)
                "medialart DATETIME " + // medialart 알림 설정 시간 datetime
                "meditake DATETIME" + // meditake 약 먹은 시간 datetime
                "call INTEGER" + // call 전화 알림 : INTEGER 1=켜짐, 0=꺼짐
                "medicycle INTEGER" + // medicycle : 복용 주기, INTEGER 1=켜짐, 0=꺼짐
                " );"
        db.execSQL(sql)
    }

    //테이블 삭제
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql : String = "DROP TABLE " + mytable
        db.execSQL(sql)
        onCreate(db)
    }
}