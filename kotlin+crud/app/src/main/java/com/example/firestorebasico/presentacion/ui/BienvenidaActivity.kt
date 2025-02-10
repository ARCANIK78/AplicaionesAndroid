package com.example.firestorebasico.presentacion.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firestorebasico.databinding.WelcomeBinding

class BienvenidaActivity: AppCompatActivity() {
    private lateinit var binding: WelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finaliza esta actividad para que no vuelva al presionar "atrás".
        }
    }
}