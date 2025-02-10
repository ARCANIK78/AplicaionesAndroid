package com.example.firestorebasico.presentacion.adaptador

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firestorebasico.data.model.ProductModel
import com.example.firestorebasico.databinding.ItemsProductoBinding

class ProductAdapter(
    private val listener: IOnClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var lista = emptyList<ProductModel>()

    interface IOnClickListener {
        fun clickEditar(productModel: ProductModel)
        fun clickEliminar(productModel: ProductModel)
    }

    inner class ProductViewHolder(private val binding: ItemsProductoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun enlazar(productModel: ProductModel) {
            binding.tvTitulo.text = productModel.nombreCompleto  // Mostrar nombre completo y CI como título
            binding.tvCodigoBarra.text = productModel.descripcion  // Mostrar descripción

            // Si se necesita mostrar la imagen:
            // Glide o cualquier librería de imágenes puede usarse aquí para manejar imagenPath
            // Glide.with(binding.root.context).load(productModel.imagenPath).into(binding.imagenProducto)

            binding.ibEditar.setOnClickListener {
                listener.clickEditar(productModel)
            }

            binding.ibEliminar.setOnClickListener {
                listener.clickEliminar(productModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemsProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.enlazar(lista[position])
    }

    fun setLista(_lista: List<ProductModel>) {
        this.lista = _lista
        notifyDataSetChanged()
    }
}
