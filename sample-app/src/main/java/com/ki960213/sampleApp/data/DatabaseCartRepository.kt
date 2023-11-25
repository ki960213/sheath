package com.ki960213.sampleApp.data

import com.ki960213.sampleApp.data.mapper.toData
import com.ki960213.sampleApp.model.CartProduct
import com.ki960213.sampleApp.model.Product
import com.ki960213.sampleApp.repository.CartRepository
import com.ki960213.sheathCore.annotation.Repository

// TODO: Step2 - CartProductDao를 참조하도록 변경
@Repository
class DatabaseCartRepository(private val dao: CartProductDao) : CartRepository {

    override suspend fun addCartProduct(product: Product) {
        dao.insert(CartProductEntity.from(product))
    }

    override suspend fun getAllCartProducts(): List<CartProduct> {
        return dao.getAll().map(CartProductEntity::toData)
    }

    override suspend fun deleteCartProduct(id: Long) {
        dao.delete(id)
    }
}
