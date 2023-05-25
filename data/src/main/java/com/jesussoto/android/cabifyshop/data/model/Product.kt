package com.jesussoto.android.cabifyshop.data.model

data class Product(
    val code: String,
    val name: String,
    val price: Double,
    val promotion: Promotion?
)
