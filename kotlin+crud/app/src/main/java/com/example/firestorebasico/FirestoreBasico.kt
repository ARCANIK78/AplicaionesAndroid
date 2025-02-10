package com.example.firestorebasico

import android.app.Application
import com.google.firebase.FirebaseApp


class FirestoreBasico : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}