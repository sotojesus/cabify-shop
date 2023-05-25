package com.jesussoto.android.cabifyshop.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.jesussoto.android.cabifyshop.data.model.Product

data class ProductWithPromotions(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "code",
        entityColumn = "productCode"
    )
    val promotions: List<PromotionEntity>
)

fun ProductWithPromotions.toExternalModel(): Product {
    val promotions = promotions.map(PromotionEntity::toExternalModel)
    return Product(product.code, product.name, product.price, promotions.firstOrNull())
}