package com.example.firestorebasico.data.common

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreInstance {

    fun get(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}