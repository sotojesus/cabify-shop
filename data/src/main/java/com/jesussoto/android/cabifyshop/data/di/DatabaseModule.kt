package com.jesussoto.android.cabifyshop.data.di

import android.content.Context
import androidx.room.Room
import com.jesussoto.android.cabifyshop.data.database.ShopDatabase
import com.jesussoto.android.cabifyshop.data.database.dao.CartDao
import com.jesussoto.android.cabifyshop.data.database.dao.ProductDao
import com.jesussoto.android.cabifyshop.data.database.dao.PromotionDao
import com.jesussoto.android.cabifyshop.data.di.qualifier.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): ShopDatabase {
        return Room.databaseBuilder(appContext, ShopDatabase::class.java, ShopDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            //.createFromAsset("cabify-shop.db")
            .build()
    }

    @Singleton
    @Provides
    fun provideProductDao(database: ShopDatabase): ProductDao {
        return database.productDao()
    }

    @Singleton
    @Provides
    fun providePromotionDao(database: ShopDatabase): PromotionDao {
        return database.promotionDao()
    }

    @Singleton
    @Provides
    fun provideCartDao(database: ShopDatabase): CartDao {
        return database.cartDao()
    }
}
