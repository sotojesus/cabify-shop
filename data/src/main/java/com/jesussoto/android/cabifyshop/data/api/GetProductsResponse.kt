package com.jesussoto.android.cabifyshop.data.api

import com.jesussoto.android.cabifyshop.data.model.Product

internal data class GetProductsResponse(val products: List<ServerProduct>)

internal data class ServerProduct(
    val code: String,

    val name: String,

    val price: Double
)

internal fun ServerProduct.toExternalModel(): Product {
    return Product(
        code,
        name,
        price,
        null
    )
}
