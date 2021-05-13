package com.example.meditime.Database

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
        var sql : String = "CREATE TABLE if not exists mytable (" +
                "_id integer primary key autoincrement," +
                "txt text);";
        db.execSQL(sql)
    }

    //테이블 삭제
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql : String = "DROP TABLE " + mytable
        db.execSQL(sql)
        onCreate(db)
    }
}