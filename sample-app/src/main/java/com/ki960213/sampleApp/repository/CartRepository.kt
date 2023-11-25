package com.ki960213.sampleApp.repository

import com.ki960213.sampleApp.model.CartProduct
import com.ki960213.sampleApp.model.Product

interface CartRepository {
    suspend fun addCartProduct(product: Product)

    suspend fun getAllCartProducts(): List<CartProduct>

    suspend fun deleteCartProduct(id: Long)
}
