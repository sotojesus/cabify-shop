package com.jesussoto.android.cabifyshop.data.cart

import android.util.Log
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.model.CartPrice
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.data.model.Promotion
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates the item-level total price for the given item based the available promotions.
 *
 * Performs the following calculations:
 * - Total item price without the promotions.
 * - Total item discounted price after applying promotions.
 * - Total promotion savings amount.
 */
@Singleton
internal class CartItemPriceCalculator @Inject constructor(
    private val discountCalculatorProvider: PromotionPriceCalculatorProvider
) {

    fun calculateItemPrice(item: CartItem): ProcessedCartItem {
        val totalItemPrice = item.product.price * item.quantity
        var totalDiscountedItemPrice = totalItemPrice
        var appliedPromotion: Promotion? = null

        item.product.promotion?.let { promotion ->
            val discountCalculator = discountCalculatorProvider.getDiscountCalculator(promotion)
            if (discountCalculator != null && discountCalculator.isApplicable(item)) {
                totalDiscountedItemPrice = discountCalculator.applyDiscount(item)
                appliedPromotion = promotion
            }
        }

        val savingsAmount = totalItemPrice - totalDiscountedItemPrice

        if (savingsAmount < 0.0) {
            // It would be very strange if we ever get a discounted price that is higher than the
            // price without discount, this would immediately tell us that there might be some sort
            // of issue with the promotions calculations, while this is not something expected to
            // happen, we might wanna emit a metric in this scenario to track if this ever happens
            // and jump quicker to debug the issue.
            // TODO: Emit a metric to track is this ever happens.
            Log.w(TAG, "savingsAmount is negative ($savingsAmount). There might be an issue with promotions price calculation.")
        }

        val price = CartPrice(totalItemPrice, totalDiscountedItemPrice, savingsAmount, appliedPromotion)
        return ProcessedCartItem(item, price)
    }

    companion object {
        private val TAG = CartItemPriceCalculator::class.simpleName
    }
}
