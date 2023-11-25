package com.ki960213.sampleApp.data.mapper

import com.ki960213.sampleApp.data.CartProductEntity
import com.ki960213.sampleApp.model.CartProduct
import com.ki960213.sampleApp.model.Product

fun CartProductEntity.toData(): CartProduct {
    return CartProduct(
        id = id,
        product = Product(
            name = name,
            price = price,
            imageUrl = imageUrl,
        ),
        createdAt = createdAt,
    )
}
