package com.jesussoto.android.cabifyshop.data.cart

import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.model.CartPrice
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Calculates the TOTAL PRICE for the cart based on the items added and the available promotions.
 *
 * Performs the following calculations:
 * - Total quantity
 * - Total price without the promotions.
 * - Total discounted price after applying promotions.
 * - Total promotion savings amount.
 *
 * In current implementation, all promotions are item-level promotions. In the scenario where we
 * need to consider also cart-level promotions, this is a good place to apply them.
 */
@Singleton
internal class CartPriceCalculator @Inject constructor(
    private val cartItemPriceCalculator: CartItemPriceCalculator
) {
    fun calculateTotalPrice(cartItems: List<CartItem>): Cart {
        val processedCartItems = cartItems.map(cartItemPriceCalculator::calculateItemPrice)

        // Filter cart items with a promotion applied.
        val discountedItems = processedCartItems
            .filter { it.price.appliedPromotion != null }
            .toSet()

        val totalQuantity = processedCartItems
            .map(ProcessedCartItem::quantity)
            .fold(0, Int::plus)

        val totalCartPrice = processedCartItems
            .map(ProcessedCartItem::price)
            .fold(CartPrice(0.0), CartPrice::plus)

        val totalSavingsAmount = abs(totalCartPrice.totalDiscountedPrice - totalCartPrice.totalPrice)

        var totalSavingsPercentage = 0.0
        if (totalCartPrice.totalDiscountedPrice > 0) {
            totalSavingsPercentage = totalSavingsAmount / totalCartPrice.totalPrice * 100
        }

        return Cart(
            processedCartItems,
            discountedItems,
            totalQuantity,
            totalCartPrice,
            totalSavingsAmount,
            totalSavingsPercentage
        )
    }
}

