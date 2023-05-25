package com.jesussoto.android.cabifyshop.data.repository

import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.model.Product
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

/**
 * Manages all Cart related operations and interactions, such adding and  removing items to cart,
 * updating quantity, and computing price and promotions.
 */
interface CartRepository {

    fun addOneToCart(product: Product): Completable

    fun updateItemQuantity(cartItem: CartItem, newQuantity: Int): Completable

    fun replaceCartItems(newItems: List<CartItem>): Completable

    fun getCartItems(): Flowable<List<CartItem>>

    fun getCart(): Flowable<Cart>

    fun getCartItemForProduct(product: Product): Flowable<CartItem>
}
