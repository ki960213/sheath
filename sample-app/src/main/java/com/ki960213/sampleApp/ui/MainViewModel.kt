package com.ki960213.sampleApp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ki960213.sheathcore.annotation.Inject
import com.github.ki960213.sheathcore.annotation.NewInstance
import com.github.ki960213.sheathcore.annotation.SheathViewModel
import com.ki960213.sampleApp.data.InMemory
import com.ki960213.sampleApp.model.Product
import com.ki960213.sampleApp.repository.CartRepository
import com.ki960213.sampleApp.repository.ProductRepository
import kotlinx.coroutines.launch

@SheathViewModel
class MainViewModel(
    @InMemory
    private val cartRepository: CartRepository,
) : ViewModel() {

    @NewInstance
    @Inject
    private lateinit var productRepository: ProductRepository

    private val _products: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _onProductAdded: MutableLiveData<Boolean> = MutableLiveData(false)
    val onProductAdded: LiveData<Boolean> get() = _onProductAdded

    fun addCartProduct(product: Product) {
        viewModelScope.launch {
            cartRepository.addCartProduct(product)
            _onProductAdded.value = true
        }
    }

    fun getAllProducts() {
        _products.value = productRepository.getAllProducts()
    }
}
