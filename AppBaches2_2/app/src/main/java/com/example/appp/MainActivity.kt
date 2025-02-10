package com.example.appp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextUpdate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Inicializar los campos de entrada
        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        editTextUpdate = findViewById(R.id.editTextId)

        // Configurar el botón para insertar datos
        val btnInsert: Button = findViewById(R.id.btnInsert)
        btnInsert.setOnClickListener { insertData() }

        // Configurar el botón para actualizar datos
        val btnUpdate: Button = findViewById(R.id.btnUpdate)
        btnUpdate.setOnClickListener { updateData() }

        // Configurar el botón para leer y mostrar los datos
        val btnRead: Button = findViewById(R.id.btnRead)
        btnRead.setOnClickListener { displayData() }

        // Configurar el botón para eliminar datos
        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener { deleteData() }
    }

    // Método para insertar datos
    private fun insertData() {
        val name = editTextName.text.toString()
        val age = editTextAge.text.toString()

        if (name.isNotBlank() && age.isNotBlank()) {
            val id = dbHelper.insertData(name, age.toInt())
            if (id > 0) {
                editTextName.text.clear()
                editTextAge.text.clear()
                hideKeyboard()
                displayData()
            }
        }
        // Ocultar el campo de actualización
        editTextUpdate.visibility = View.GONE
    }

    // Método para mostrar datos en un ListView
    private fun displayData() {
        val cursor = dbHelper.readData()
        cursor.use {
            val columns = arrayOf(DatabaseHelper.COL_NAME, DatabaseHelper.COL_AGE)
            val toViews = intArrayOf(R.id.textName, R.id.textAge)
            val adapter = SimpleCursorAdapter(this, R.layout.list_item, it, columns, toViews, 0)
            val listView = findViewById<ListView>(R.id.listView)
            listView.adapter = adapter
        }
    }

    // Método para ocultar el teclado
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // Método para actualizar datos
    private fun updateData() {
        editTextUpdate.visibility = View.VISIBLE
        val name = editTextName.text.toString()
        val age = editTextAge.text.toString()
        val id = editTextUpdate.text.toString()

        if (name.isNotBlank() && age.isNotBlank() && id.isNotBlank()) {
            // Usar id como entero
            val updatedRows = dbHelper.updateData(id.toInt(), name, age.toInt())
            if (updatedRows > 0) {
                editTextName.text.clear()
                editTextAge.text.clear()
                editTextUpdate.text.clear()
                hideKeyboard()
                displayData()
            }
        }
    }

    // Método para eliminar datos
    private fun deleteData() {
        editTextUpdate.visibility = View.VISIBLE
        val id = editTextUpdate.text.toString()

        if (id.isNotBlank()) {
            // Usar id como entero
            val deletedRows = dbHelper.deleteData(id.toInt())
            if (deletedRows > 0) {
                editTextName.text.clear()
                editTextAge.text.clear()
                editTextUpdate.text.clear()
                hideKeyboard()
                displayData()
            }
        }
    }
}

