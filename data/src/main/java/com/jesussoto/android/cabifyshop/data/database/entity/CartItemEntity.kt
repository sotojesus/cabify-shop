package com.jesussoto.android.cabifyshop.data.database.entity;

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jesussoto.android.cabifyshop.data.model.CartItem

@Entity(
    tableName = "cart_item",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("code"),
            childColumns = arrayOf("productCode"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class CartItemEntity(
    @PrimaryKey
    val productCode: String,

    val quantity: Int
) {
    constructor(item: CartItem): this(item.product.code, item.quantity)
}
