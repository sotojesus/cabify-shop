package com.jesussoto.android.cabifyshop.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.jesussoto.android.cabifyshop.data.model.CartItem

data class CartItemWithProduct(
    @Embedded val cartItem: CartItemEntity,
    @Relation(
        parentColumn = "productCode",
        entityColumn = "code",
        entity = ProductEntity::class
    )
    val product: ProductWithPromotions
)

fun CartItemWithProduct.toExternalModel(): CartItem {
    return CartItem(
        this.product.toExternalModel(),
        this.cartItem.quantity
    )
}