package com.jesussoto.android.cabifyshop.data.api

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

internal interface CabifyShopServiceAPI {

    @GET("Products.json")
    fun getProducts(): Single<GetProductsResponse>
}
