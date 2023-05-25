package com.jesussoto.android.cabifyshop.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jesussoto.android.cabifyshop.data.database.entity.ProductEntity
import com.jesussoto.android.cabifyshop.data.database.entity.ProductWithPromotions
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductEntity>): Completable

    @Delete
    fun delete(product: ProductEntity)

    @Query("DELETE FROM product WHERE 1")
    fun clearTable(): Completable

    @Transaction
    @Query("SELECT * FROM product")
    fun getProductsWithPromotions(): Flowable<List<ProductWithPromotions>>

    @Transaction
    @Query("SELECT * FROM product WHERE product.code = :code")
    fun getProductWithPromotions(code: String): Flowable<ProductWithPromotions>
}
