package com.example.firestorebasico.presentacion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.firestorebasico.presentacion.common.makeCall
import android.content.ContentValues // Asegúrate de importar ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firestorebasico.R
import com.example.firestorebasico.data.model.ProductModel
import com.example.firestorebasico.data.repository.ProductRepository
import com.example.firestorebasico.databinding.ActivityOperacionProductMainBinding
import com.example.firestorebasico.presentacion.common.UiState
import pe.pcs.libpcs.UtilsCommon
import pe.pcs.libpcs.UtilsMessage
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class OperacionProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperacionProductMainBinding
    private var _id = ""
    private var imageUri: Uri? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    // Usando ActivityResultContracts para tomar una foto
    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                imageUri?.let {
                    binding.ivImagen.setImageURI(it) // Verificar que la URI esté siendo pasada correctamente
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOperacionProductMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

        if (intent.extras != null)
            obtenerProducto()
    }

    private fun initListener() {
        binding.includeToolbar.toolbar.apply {
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            subtitle = "Registrar | Editar Publicacion"
            navigationIcon = AppCompatResources.getDrawable(
                this@OperacionProductoActivity, R.drawable.baseline_arrow_back_24
            )
        }

        binding.includeToolbar.ibAccion.setImageResource(R.drawable.baseline_done_all_24)

        binding.includeToolbar.ibAccion.setOnClickListener {
            if (binding.etNombreCompleto.text.toString().trim().isEmpty() ||
                binding.etDescripcion.text.toString().trim().isEmpty() ||
                binding.etCi.text.toString().trim().isEmpty() || // Verificar que el CI no esté vacío
                imageUri == null
            ) {
                UtilsMessage.showAlertOk(
                    "ERROR", "Debe llenar todos los campos y tomar una foto", this@OperacionProductoActivity
                )
                return@setOnClickListener
            }

            // Obtener la localización antes de grabar el producto
            obtenerLocalizacion { lat, lon ->
                latitude = lat
                longitude = lon

                grabar(
                    ProductModel(
                        id = _id,
                        nombreCompleto = binding.etNombreCompleto.text.toString(),
                        descripcion = binding.etDescripcion.text.toString(),
                        imagenPath = imageUri.toString(),  // Asegúrate de que imageUri sea válida aquí
                        ci = binding.etCi.text.toString(),
                        latitud = latitude,
                        longitud = longitude
                    )
                )
            }
        }

        binding.btnTomarFoto.setOnClickListener {
            abrirCamara()
        }
    }

    private fun obtenerLocalizacion(callback: (Double, Double) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verificar permisos antes de intentar obtener la localización
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    // Llamar al callback con la latitud y longitud
                    callback(it.latitude, it.longitude)
                } ?: run {
                    UtilsMessage.showAlertOk("ERROR", "No se pudo obtener la localización", this)
                }
            }
        } else {
            // Solicitar permisos si no están otorgados
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun obtenerProducto() {
        _id = intent.extras?.getString("id") ?: ""
        binding.etNombreCompleto.setText(intent.extras?.getString("nombreCompleto"))
        binding.etDescripcion.setText(intent.extras?.getString("descripcion"))
        binding.etCi.setText(intent.extras?.getString("ci")) // Obtener el CI al editar
        val imageUriString = intent.extras?.getString("imagenUri")
        if (!imageUriString.isNullOrEmpty()) {
            imageUri = Uri.parse(imageUriString)
            binding.ivImagen.setImageURI(imageUri)
        }
    }

    private fun abrirCamara() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Nueva imagen")
            put(MediaStore.Images.Media.DESCRIPTION, "Imagen tomada desde la cámara")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            takePicture.launch(it) // Lanza la cámara solo si la URI es válida
        } ?: UtilsMessage.showAlertOk("ERROR", "No se pudo generar la URI de la imagen", this)
    }

    private fun grabar(model: ProductModel) = lifecycleScope.launch {
        binding.progressBar.isVisible = true

        makeCall { ProductRepository().grabar(model) }.let {
            when (it) {
                is UiState.Error -> {
                    binding.progressBar.isVisible = false
                    UtilsMessage.showAlertOk(
                        "ERROR", it.message, this@OperacionProductoActivity
                    )
                }

                is UiState.Success -> {
                    binding.progressBar.isVisible = false
                    UtilsMessage.showToast(this@OperacionProductoActivity, "Registro grabado")
                    UtilsCommon.cleanEditText(binding.root.rootView)
                    UtilsCommon.hideKeyboard(this@OperacionProductoActivity, binding.root.rootView)
                    binding.etNombreCompleto.requestFocus()
                    _id = ""
                    MainActivity.existeCambio = true
                }
            }
        }
    }
}

