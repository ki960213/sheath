package com.ki960213.sampleApp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ki960213.sampleApp.R
import com.ki960213.sampleApp.databinding.ActivityMainBinding
import com.ki960213.sampleApp.ui.cart.CartActivity
import com.ki960213.sheathandroid.extention.viewModels

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBinding()
        setupToolbar()
        setupView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        menu?.findItem(R.id.cart)?.actionView?.let { view ->
            view.setOnClickListener { navigateToCart() }
        }
        return true
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupView() {
        setupProductData()
        setupProductList()
    }

    private fun setupProductData() {
        viewModel.getAllProducts()
    }

    private fun setupProductList() {
        viewModel.products.observe(this) {
            val adapter = ProductAdapter(
                items = it,
                onClickProduct = viewModel::addCartProduct,
            )
            binding.rvProducts.adapter = adapter
        }
        viewModel.onProductAdded.observe(this) {
            if (!it) return@observe
            Toast.makeText(this, getString(R.string.cart_added), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToCart() {
        startActivity(Intent(this, CartActivity::class.java))
    }
}
