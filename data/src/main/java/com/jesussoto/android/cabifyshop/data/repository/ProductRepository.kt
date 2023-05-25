package com.jesussoto.android.cabifyshop.data.repository

import com.jesussoto.android.cabifyshop.data.model.Product
import io.reactivex.rxjava3.core.Flowable

/**
 * Manages all [Product] related operations and interactions, such as basic CRUD operations and
 * abstracts backing storage implementation
 */
interface ProductRepository : Syncable {

    fun getProducts(): Flowable<List<Product>>

    fun getProduct(productCode: String): Flowable<Product>
}
