package com.jesussoto.android.cabifyshop.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jesussoto.android.cabifyshop.data.database.entity.CartItemEntity
import com.jesussoto.android.cabifyshop.data.database.entity.CartItemWithProduct
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
internal interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSynchronous(cartItem: CartItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<CartItemEntity>): Completable

    @Update
    fun updateSynchronous(cartItem: CartItemEntity)

    @Delete
    fun deleteSynchronous(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_item WHERE 1")
    fun clearTable(): Completable

    @Query("SELECT * FROM cart_item WHERE productCode = :productCode")
    fun getCartItemForProductSynchronous(productCode: String): CartItemEntity?

    @Query("SELECT * FROM cart_item WHERE productCode = :productCode")
    fun getCartItemForProduct(productCode: String): Flowable<CartItemWithProduct>

    @Query("SELECT * FROM cart_item")
    fun getCartItemsWithProduct(): Flowable<List<CartItemWithProduct>>
}
