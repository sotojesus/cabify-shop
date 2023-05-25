package com.jesussoto.android.cabifyshop.data.repository

import com.jesussoto.android.cabifyshop.data.model.Promotion
import io.reactivex.rxjava3.core.Flowable

/**
 * Manages all [Promotion] related operations and abstract backing store implementation.
 */
interface PromotionRepository: Syncable {

    fun getPromotions(): Flowable<List<Promotion>>
}
