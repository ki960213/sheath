package com.ki960213.sampleApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ki960213.sampleApp.databinding.ItemProductBinding
import com.ki960213.sampleApp.model.Product

class ProductViewHolder(
    private val binding: ItemProductBinding,
    onClickProduct: (Product) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onClickProduct(binding.item ?: return@setOnClickListener)
        }
    }

    fun bind(product: Product) {
        binding.item = product
    }

    companion object {
        fun from(parent: ViewGroup, onClickProduct: (Product) -> Unit): ProductViewHolder {
            val binding =
                ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ProductViewHolder(binding, onClickProduct)
        }
    }
}
