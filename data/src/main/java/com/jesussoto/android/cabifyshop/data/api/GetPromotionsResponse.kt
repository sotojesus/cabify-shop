package com.jesussoto.android.cabifyshop.data.api

import com.google.gson.JsonObject
import com.jesussoto.android.cabifyshop.data.model.Promotion
import com.jesussoto.android.cabifyshop.data.model.PromotionType

/**
 * FAKE GetPromotions response, kept with the same contract and schema as [GetProductsResponse]
 * for consistency.
 */
internal data class GetPromotionsResponse(val promotions: List<ServerPromotion>)

internal data class ServerPromotion(
    val productCode: String,

    val type: String,

    val config: JsonObject
)

@Throws
// TODO: Check for exception when PromotionType.valueOf() fails.
internal fun ServerPromotion.toExternalModel(): Promotion {
    return Promotion(
        productCode,
        PromotionType.valueOf(type),
        config
    )
}
