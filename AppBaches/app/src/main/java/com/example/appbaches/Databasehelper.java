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
        const val TABLE_NAME = "users"  // Definir el nombre de la tabla
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla en la base de datos
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_AGE INTEGER
            );
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Si la base de datos ya existe y se actualiza, eliminamos la tabla anterior y la recreamos
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    // Método para insertar datos
    fun insertData(name: String, age: Int): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_NAME, name)
        values.put(COL_AGE, age)
        return db.insert(TABLE_NAME, null, values)
    }

    // Método para actualizar datos
    fun updateData(id: String, name: String, age: Int): Int {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_NAME, name)
        values.put(COL_AGE, age)
        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(id))
    }

    // Método para eliminar datos
    fun deleteData(id: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id))
    }
}

