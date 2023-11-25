package com.ki960213.sampleApp.repository

import com.ki960213.sampleApp.model.Product

interface ProductRepository {
    fun getAllProducts(): List<Product>
}
