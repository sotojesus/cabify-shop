package com.jesussoto.android.cabifyshop.data.cart

/**
 * Configuration for "Wholesales reduced unit price" promotions.
 */
data class FixedBulkPricePromotionConfig(
    val minApplicableQuantity: Int,
    val discountedUnitPrice: Double
)

/**
 * Configuration for "But X, Get X" promotions.
 */
data class BuyXGetXPromotionConfig(
    val minApplicableQuantity: Int,
    val quantityToGetForFree: Int
)
