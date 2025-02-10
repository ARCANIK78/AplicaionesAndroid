package com.example.firestorebasico.data.model

data class ProductModel(
    var id: String = "",
    val nombreCompleto: String = "",
    val ci: String = "",
    val descripcion: String = "",
    val imagenPath: String = "",
    val latitud: Double? = null,  // Cambié de 'val' a 'var'
    val longitud: Double? = null // Cambié de 'val' a 'var'
) {
    constructor() : this("", "", "", "", "", null, null) // Constructor sin parámetros con valores predeterminados


}


