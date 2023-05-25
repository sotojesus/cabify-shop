package com.jesussoto.android.cabifyshop.data.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jesussoto.android.cabifyshop.data.cart.BuyXGetXPromotionConfig
import com.jesussoto.android.cabifyshop.data.cart.FixedBulkPricePromotionConfig

data class Promotion(
    val productCode: String,

    val type: PromotionType,

    val config: JsonObject
)

enum class PromotionType(val configClass: Class<*>) {
    FixedBulkPricePromotion(FixedBulkPricePromotionConfig::class.java),
    BuyXGetXPromotion(BuyXGetXPromotionConfig::class.java)
}

fun <T> Promotion.typedConfig(): T {
    return promoConfigGson.fromJson(config, type.configClass) as T
}

private val promoConfigGson = Gson()
