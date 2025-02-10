package com.example.appp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "MyDatabase.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        const val COL_ID = "_id"
        const val COL_NAME = "name"
        const val COL_AGE = "age"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_AGE INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Método para insertar datos
    fun insertData(name: String, age: Int): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_NAME, name)
                put(COL_AGE, age)
            }
            db.insert(TABLE_NAME, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
            -1 // Retorna -1 si hay algún error
        }
    }

    // Método para leer datos
    fun readData(): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_NAME,
            arrayOf(COL_ID, COL_NAME, COL_AGE),
            null, null, null, null,
            "$COL_NAME ASC"
        )
    }

    // Método para actualizar datos
    fun updateData(id: Int, name: String, age: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_AGE, age)
        }
        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(id.toString()))
    }

    // Método para eliminar datos
    fun deleteData(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))
    }
}
