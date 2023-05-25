package com.jesussoto.android.cabifyshop.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesussoto.android.cabifyshop.data.model.Product

@Entity(tableName = "product")
data class ProductEntity(
    @PrimaryKey
    val code: String,

    val name: String,

    val price: Double,
) {
    constructor(product: Product) : this(product.code, product.name, product.price)
}


