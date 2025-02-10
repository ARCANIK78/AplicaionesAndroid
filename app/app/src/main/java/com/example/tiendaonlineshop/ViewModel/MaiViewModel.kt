package com.example.tiendaonlineshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tiendaonlineshop.model.sliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase

class MaiViewModel : ViewModel() {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _banner = MutableLiveData<List<sliderModel>>()
    val banners: LiveData<List<sliderModel>> get() = _banner

    fun loadBanners() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<sliderModel>()
                for (childSnapshot in snapshot.children) {
                    val model = childSnapshot.getValue(sliderModel::class.java)
                    if (model != null) {
                        list.add(model)
                    }
                }
                _banner.value = list // Actualiza la lista en el LiveData
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo del error de Firebase
                println("Error: ${error.message}")
            }
        })
    }
}
