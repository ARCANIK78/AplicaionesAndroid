package com.example.firestorebasico.data.repository

import kotlinx.coroutines.tasks.await
import com.example.firestorebasico.data.common.FirestoreCnnstants
import com.example.firestorebasico.data.common.FirestoreInstance
import com.example.firestorebasico.data.model.ProductModel

class ProductRepository {

    // Listar productos según un dato (por ejemplo, filtro de descripción)
    suspend fun listar(dato: String): List<ProductModel> {
        return FirestoreInstance.get().collection(FirestoreCnnstants.COLECCION_PRODUCTO)
            .orderBy("descripcion")
            .startAt(dato)
            .endAt(dato + "\uf8ff")
            .get()
            .await()
            .toObjects(ProductModel::class.java)
    }

    // Registrar un nuevo producto en Firestore
    public suspend fun registrar(model: ProductModel): Boolean {
        val documento = FirestoreInstance.get().collection(FirestoreCnnstants.COLECCION_PRODUCTO).document()
        model.id = documento.id // Generamos un ID para el producto
        documento.set(model).await() // Guardamos el modelo
        return true
    }

    // Actualizar un producto existente en Firestore
    private suspend fun actualizar(model: ProductModel): Boolean {
        val documento = FirestoreInstance.get().collection(FirestoreCnnstants.COLECCION_PRODUCTO)
            .document(model.id)

        // Actualizamos la descripción, nombre completo e imagen (URI de la imagen)
        documento.update(
            mapOf(
                "descripcion" to model.descripcion,
                "nombreCompleto" to model.nombreCompleto,
                "imagenUri" to model.imagenPath,
                "ci" to model.ci  // Actualiza el CI si es necesario
            )
        ).await()

        return true
    }

    // Eliminar un producto de Firestore
    suspend fun eliminar(model: ProductModel): Boolean {
        val documento = FirestoreInstance.get().collection(FirestoreCnnstants.COLECCION_PRODUCTO)
            .document(model.id)
        documento.delete().await() // Eliminamos el documento

        return true
    }

    // Grabar (guardar o actualizar) un producto
    suspend fun grabar(model: ProductModel): Boolean {
        return if (model.id.isEmpty()) {
            // Si el producto no tiene ID, lo registramos como nuevo
            registrar(model)
        } else {
            // Si tiene ID, lo actualizamos
            actualizar(model)
        }
    }
}

