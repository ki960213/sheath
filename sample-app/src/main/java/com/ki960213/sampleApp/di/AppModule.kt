package com.ki960213.sampleApp.di

import android.content.Context
import androidx.room.Room
import com.github.ki960213.sheathcore.annotation.Component
import com.github.ki960213.sheathcore.annotation.Module
import com.ki960213.sampleApp.data.CartProductDao
import com.ki960213.sampleApp.data.ShoppingDatabase

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
