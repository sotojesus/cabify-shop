package com.jesussoto.android.cabifyshop.data.model

data class CartPrice(

    val totalPrice: Double,

    val totalDiscountedPrice: Double = totalPrice,

    val totalSavingsAmount: Double = 0.0,

    val appliedPromotion: Promotion? = null
) {
    operator fun plus(other: CartPrice): CartPrice {
        return CartPrice(
            totalPrice + other.totalPrice,
            totalDiscountedPrice + other.totalDiscountedPrice,
            totalSavingsAmount + other.totalSavingsAmount,
            null)
    }
}
