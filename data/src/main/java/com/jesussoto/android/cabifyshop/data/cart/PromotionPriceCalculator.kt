package com.jesussoto.android.cabifyshop.data.cart

import com.jesussoto.android.cabifyshop.data.model.CartItem

/**
 * Specifies item-level price calculation logic for an item with promotion.
 */
interface PromotionPriceCalculator {

    /**
     * Is this promotion applicable for the current cart item values?
     */
    fun isApplicable(item: CartItem): Boolean

    /**
     * Calculate promotional discount for the given cart item.
     */
    fun applyDiscount(item: CartItem): Double
}

/**
 * Price calculation logic for products with "Fixed Bulk Price" promotions.
 *
 * The calculation if base solely on the number of items in the cart, at a glance, it is:
 *
 * discountedPrice = quantityOfProductsInCart * reducedUnitPrice
 */
class FixedBulkPricePromotionCalculator(
    private val config: FixedBulkPricePromotionConfig
): PromotionPriceCalculator {

    override fun isApplicable(item: CartItem): Boolean {
        return item.quantity >= config.minApplicableQuantity
    }

    override fun applyDiscount(item: CartItem): Double {
        val totalPriceWithoutDiscount = item.product.price * item.quantity
        if (!isApplicable(item)) {
            return totalPriceWithoutDiscount
        }

        return config.discountedUnitPrice * item.quantity
    }
}

/**
 * Price calculation logic for products with "Buy X, Get X" promotions.
 *
 * The calculation if base solely on the number of items in the cart in comparison with the
 * provided config.
 *
 * This same logic can be used for promotions of type:
 * - "Buy 1, Get 1 Free" (2-for-1), // 1-1
 * - "Buy 2, Get 1 Free" (3-for-2), // 3-1
 * - "Buy 3, Get 2 Free" (5-for-3)  // 5-2
 * - And so on...
 */
class ButXGetXPromotionCalculator(
    private val config: BuyXGetXPromotionConfig
): PromotionPriceCalculator {

    override fun isApplicable(item: CartItem): Boolean {
        return item.quantity >= config.minApplicableQuantity
    }

    override fun applyDiscount(item: CartItem): Double {
        val totalPriceWithoutDiscount = item.product.price * item.quantity
        if (!isApplicable(item)) {
            return totalPriceWithoutDiscount
        }

        val quantityToPayFor = config.minApplicableQuantity - config.quantityToGetForFree
        val reducedQuantity = item.quantity / config.minApplicableQuantity * quantityToPayFor
        val reminderQuantity = item.quantity % config.minApplicableQuantity

        return (reducedQuantity + reminderQuantity) * item.product.price
    }
}
