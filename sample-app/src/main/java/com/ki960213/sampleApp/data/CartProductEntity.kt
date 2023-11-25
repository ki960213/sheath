package com.ki960213.sampleApp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ki960213.sampleApp.model.Product
import java.time.LocalDateTime

@Entity(tableName = "cart_products")
data class CartProductEntity(val name: String, val price: Int, val imageUrl: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var createdAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun from(product: Product): CartProductEntity = CartProductEntity(
            name = product.name,
            price = product.price,
            imageUrl = product.imageUrl,
        )
    }
}
