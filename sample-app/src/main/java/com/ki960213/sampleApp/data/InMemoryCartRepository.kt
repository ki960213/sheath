package com.ki960213.sampleApp.data

import com.ki960213.sampleApp.model.CartProduct
import com.ki960213.sampleApp.model.Product
import com.ki960213.sampleApp.repository.CartRepository
import com.ki960213.sheathCore.annotation.Qualifier
import com.ki960213.sheathCore.annotation.Repository
import java.time.LocalDateTime

@Qualifier
annotation class InMemory

@InMemory
@Repository
class InMemoryCartRepository : CartRepository {
    private val cartProducts: MutableList<CartProduct> = mutableListOf()

    private var nextId: Long = 0

    override suspend fun addCartProduct(product: Product) {
        val cartProduct = CartProduct(
            id = ++nextId,
            product = product,
            createdAt = LocalDateTime.now(),
        )
        cartProducts.add(cartProduct)
    }

    override suspend fun getAllCartProducts(): List<CartProduct> {
        return cartProducts.toList()
    }

    override suspend fun deleteCartProduct(id: Long) {
        cartProducts.removeIf { it.id == id }
    }
}
