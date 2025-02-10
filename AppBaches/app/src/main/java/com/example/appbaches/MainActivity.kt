package com.example.appbaches

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "MyDatabase"
        const val DATABASE_VERSION = 1
        const val COL_ID = "_id"
        const val COL_NAME = "name"
        const val COL_AGE = "age"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla en la base de datos
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_AGE INTEGER
            );
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si la base de datos ya existe y se actualiza, eliminamos la tabla anterior y la recreamos
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db!!)
    }
    fun insertData(name: String, age:Int):long{
        val=db=writableDatabase
        val values=ContentValues()
        values.put(COL_NAME,name)
        values.put(COL_AGE,age)
        return db.insert(TABLE_NAME, null,values)
    }
}
