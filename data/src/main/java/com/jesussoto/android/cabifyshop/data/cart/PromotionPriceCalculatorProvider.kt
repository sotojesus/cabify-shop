package com.jesussoto.android.cabifyshop.data.cart

import android.util.Log
import com.jesussoto.android.cabifyshop.data.model.Promotion
import com.jesussoto.android.cabifyshop.data.model.PromotionType
import com.jesussoto.android.cabifyshop.data.model.typedConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides the appropriate [PromotionPriceCalculator] for the given [Promotion] based on
 * its type and config.
 *
 * Error Handling:
 *  - If any exception occurs while obtaining the [PromotionPriceCalculator], we return `null` to
 *    avoid crashing the app, which would result in the promotion not being applied for the
 *    underlying [CartItem].
 */
@Singleton
internal class PromotionPriceCalculatorProvider @Inject constructor() {

    fun getDiscountCalculator(promotion: Promotion): PromotionPriceCalculator? {
        return try {
            when(promotion.type) {
                PromotionType.FixedBulkPricePromotion ->
                    FixedBulkPricePromotionCalculator(promotion.typedConfig())

                PromotionType.BuyXGetXPromotion ->
                    ButXGetXPromotionCalculator(promotion.typedConfig())
            }
        } catch (th: Throwable) {
            // In case we are not able to parse the config, we probably don't want to crash the App,
            // but instead treat the current item as an item without promotion. This may happen if
            // the server sends a promotion that is unknown by the current version of the App.
            // Thus, returning 'null' to ensure backwards compatibility.

            // TODO: Log error to crashlytics to identify the non-parseable config.
            // TODO: Emit a metric to track how often this happen and set an alarm on it.
            Log.e(TAG, "Error while getting promotion calculator for promotion: '$promotion'", th)
            null
        }
    }

    companion object {
        private val TAG = PromotionPriceCalculatorProvider::class.simpleName
    }
}
