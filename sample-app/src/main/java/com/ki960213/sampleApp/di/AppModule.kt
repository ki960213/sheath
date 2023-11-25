package com.ki960213.sampleApp.di

import android.content.Context
import androidx.room.Room
import com.ki960213.sampleApp.data.CartProductDao
import com.ki960213.sampleApp.data.ShoppingDatabase
import com.ki960213.sheathCore.annotation.Component
import com.ki960213.sheathCore.annotation.Module

@Module
object AppModule {

    @Component
    fun getShoppingDatabase(context: Context): ShoppingDatabase = Room.databaseBuilder(
        context.applicationContext,
        ShoppingDatabase::class.java,
        "shopping_db.db",
    ).build()

    @Component
    fun getCartProductDao(shoppingDatabase: ShoppingDatabase): CartProductDao =
        shoppingDatabase.cartProductDao()
}
