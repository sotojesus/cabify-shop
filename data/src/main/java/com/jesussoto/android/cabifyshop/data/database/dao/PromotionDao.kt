package com.jesussoto.android.cabifyshop.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jesussoto.android.cabifyshop.data.database.entity.PromotionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PromotionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(promotion: List<PromotionEntity>): Completable

    @Delete
    fun deleteSynchronous(promotion: PromotionEntity): Completable

    @Query("DELETE FROM promotion WHERE 1")
    fun clearTable(): Completable

    @Query("SELECT * FROM promotion")
    fun getPromotions(): Flowable<List<PromotionEntity>>

    @Query("SELECT * FROM promotion WHERE productCode = :productCode")
    fun getPromotionsForProduct(productCode: String): Flowable<PromotionEntity>
}
