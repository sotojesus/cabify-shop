package com.jesussoto.android.cabifyshop.data.model

data class CartItem(
    val product: Product,

    val quantity: Int,
)

data class ProcessedCartItem(
    val product: Product,

    val quantity: Int,

    val price: CartPrice,
) {
    constructor(cartItem: CartItem, price: CartPrice) :
            this(cartItem.product, cartItem.quantity, price)
}

fun ProcessedCartItem.asBaseCartItem(): CartItem {
    return CartItem(product, quantity)
}
