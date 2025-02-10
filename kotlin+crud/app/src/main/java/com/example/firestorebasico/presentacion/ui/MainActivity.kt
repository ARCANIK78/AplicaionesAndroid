package com.example.firestorebasico.presentacion.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestorebasico.data.model.ProductModel
import com.example.firestorebasico.data.repository.ProductRepository
import com.example.firestorebasico.databinding.ActivityMainBinding
import com.example.firestorebasico.presentacion.adaptador.ProductAdapter
import com.example.firestorebasico.presentacion.common.UiState
import com.example.firestorebasico.presentacion.common.makeCall
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import pe.pcs.libpcs.UtilsCommon
import pe.pcs.libpcs.UtilsMessage
import android.provider.MediaStore



class MainActivity : AppCompatActivity(), ProductAdapter.IOnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val CAMERA_REQUEST_CODE = 1001  // Definir el código de solicitud

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                obtenerUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        solicitarPermisoUbicacion()
        initListener()
        leerProducto("")
    }

    override fun onResume() {
        super.onResume()
        if (existeCambio) {
            existeCambio = false
            leerProducto(binding.etBuscar.text.toString().trim())
        }
    }

    private fun initListener() {
        binding.includeToolbar.ibAccion.setOnClickListener {
            startActivity(Intent(this, OperacionProductoActivity::class.java))
            solicitarPermisoUbicacion()
        }

        binding.rvLista.apply {
            adapter = ProductAdapter(this@MainActivity)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.tilBuscar.setEndIconOnClickListener {
            leerProducto(binding.etBuscar.text.toString().trim())
            UtilsCommon.hideKeyboard(this@MainActivity, it)
        }

    }

    private fun solicitarPermisoUbicacion() {
        when {
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                obtenerUbicacion()
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // El permiso fue concedido, puedes usar la cámara
                usarCamara()
            } else {
                // El permiso fue denegado, puedes mostrar un mensaje al usuario
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun usarCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun solicitarPermisoCamara() {
        when {
            // Verificamos si el permiso ya está concedido
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Si el permiso está concedido, puedes usar la cámara
                usarCamara()
            }
            else -> {
                // Si el permiso no está concedido, solicitamos el permiso
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun obtenerUbicacion() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Toast.makeText(this, "Ubicación: ${location.latitude}, ${location.longitude}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun leerProducto(dato: String) = lifecycleScope.launch {
        binding.progressBar.isVisible = true
        makeCall { ProductRepository().listar(dato) }.let {
            binding.progressBar.isVisible = false
            when (it) {
                is UiState.Error -> UtilsMessage.showAlertOk("ERROR", it.message, this@MainActivity)
                is UiState.Success -> (binding.rvLista.adapter as ProductAdapter).setLista(it.data)
            }
        }
    }

    private fun eliminar(model: ProductModel) = lifecycleScope.launch {
        binding.progressBar.isVisible = true
        makeCall { ProductRepository().eliminar(model) }.let {
            binding.progressBar.isVisible = false
            if (it is UiState.Success) {
                UtilsMessage.showToast(this@MainActivity, "Registro eliminado")
                leerProducto(binding.etBuscar.text.toString().trim())
            } else if (it is UiState.Error) {
                UtilsMessage.showAlertOk("ERROR", it.message, this@MainActivity)
            }
        }
    }

    override fun clickEditar(productModel: ProductModel) {
        startActivity(Intent(this, OperacionProductoActivity::class.java).apply {
            putExtra("id", productModel.id)
            putExtra("nombreCompleto", productModel.nombreCompleto)
            putExtra("descripcion", productModel.descripcion)
            putExtra("ci", productModel.ci)
            putExtra("imagenPath", productModel.imagenPath)
        })
        startActivity(intent)

    }

    override fun clickEliminar(productModel: ProductModel) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Eliminar")
            setMessage("¿Desea eliminar el registro ${productModel.descripcion}?")
            setPositiveButton("SI") { dialog, _ ->
                eliminar(productModel)
                dialog.dismiss()
            }
            setNegativeButton("Cancelar", null)
        }.create().show()
    }

    companion object {
        var existeCambio = false
    }
}
