package com.jesussoto.android.cabifyshop.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jesussoto.android.cabifyshop.data.database.dao.CartDao
import com.jesussoto.android.cabifyshop.data.database.dao.ProductDao
import com.jesussoto.android.cabifyshop.data.database.dao.PromotionDao
import com.jesussoto.android.cabifyshop.data.database.entity.CartItemEntity
import com.jesussoto.android.cabifyshop.data.database.entity.ProductEntity
import com.jesussoto.android.cabifyshop.data.database.entity.PromotionEntity

@Database(
    entities = [
        ProductEntity::class,
        PromotionEntity::class,
        CartItemEntity::class
    ],
    version = 1
)
internal abstract class ShopDatabase: RoomDatabase() {

    internal abstract fun productDao(): ProductDao

    internal abstract fun promotionDao(): PromotionDao

    internal abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "cabify-shop.db"
    }
}
