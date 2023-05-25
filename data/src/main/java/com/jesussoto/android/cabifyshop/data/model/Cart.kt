package com.jesussoto.android.cabifyshop.data.model

data class Cart(

    val items: List<ProcessedCartItem>,

    val discountedItems: Set<ProcessedCartItem>,

    val totalItemQuantity: Int,

    val price: CartPrice,

    val totalSavingsAmount: Double,

    val totalSavingsPercentage: Double,
)
