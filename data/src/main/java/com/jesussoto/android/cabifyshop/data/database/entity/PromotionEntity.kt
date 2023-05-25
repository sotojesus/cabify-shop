package com.jesussoto.android.cabifyshop.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.jesussoto.android.cabifyshop.data.model.Promotion
import com.jesussoto.android.cabifyshop.data.model.PromotionType

@Entity(
    tableName = "promotion",
    // This table was initially considers to support multiple promotions of the same type for a
    // given product, that's why "configJson" is part of the primary key. But the for current scope
    // of the App which only supports one-to-one product to promotion relationship, it can be safely
    // removed and leave the PK as (productCode, type)
    primaryKeys = ["productCode", "type", "configJson"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("code"),
            childColumns = arrayOf("productCode"),
            onDelete = ForeignKey.CASCADE)
    ]
)
data class PromotionEntity(
    val productCode: String,

    val type: String,

    val configJson: String
) {
    constructor(promotion: Promotion) : this(
        promotion.productCode,
        promotion.type.name,
        gson.toJson(promotion.config))
}

@Throws(JsonParseException::class)
fun PromotionEntity.toExternalModel(): Promotion {
    // TODO: Address risk of IllegalArgumentException (it is really needed considering we have
    //  control over which value end-up in the database? Perhaps for DB migrations?
    return Promotion(
        productCode,
        PromotionType.valueOf(type),
        jsonParser.parse(configJson).asJsonObject
    )
}

// TODO: Provide externally?
private val gson = Gson()
private val jsonParser = JsonParser()
