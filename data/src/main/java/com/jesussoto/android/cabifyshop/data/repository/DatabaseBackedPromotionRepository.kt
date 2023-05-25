package com.jesussoto.android.cabifyshop.data.repository

import android.util.Log
import com.jesussoto.android.cabifyshop.data.api.GetPromotionsResponse
import com.jesussoto.android.cabifyshop.data.api.PromotionsDataSource
import com.jesussoto.android.cabifyshop.data.api.ServerPromotion
import com.jesussoto.android.cabifyshop.data.api.toExternalModel
import com.jesussoto.android.cabifyshop.data.database.dao.PromotionDao
import com.jesussoto.android.cabifyshop.data.database.entity.PromotionEntity
import com.jesussoto.android.cabifyshop.data.database.entity.toExternalModel
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.IO
import com.jesussoto.android.cabifyshop.data.model.Promotion
import com.jesussoto.android.cabifyshop.data.model.PromotionType
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

internal class DatabaseBackedPromotionRepository @Inject internal constructor(
    private val dataSource: PromotionsDataSource,
    private val promotionDao: PromotionDao,
    @RxScheduler(IO) private val scheduler: Scheduler
): PromotionRepository {

    override fun getPromotions(): Flowable<List<Promotion>> {
        return promotionDao.getPromotions()
            .map { it.map(PromotionEntity::toExternalModel) }
            .subscribeOn(scheduler)
    }

    override fun sync(): Completable {
        return dataSource.getPromotions()
            .map(GetPromotionsResponse::promotions)
            .map(this::transformToExternalPromotions)
            .map { it.map(::PromotionEntity) }
            .flatMapCompletable(this::replacePromotions)
    }

    private fun replacePromotions(newPromotions: List<PromotionEntity>): Completable {
        return promotionDao.clearTable()
            .andThen(promotionDao.insertAll(newPromotions))
    }

    private fun transformToExternalPromotions(promotions: List<ServerPromotion>): List<Promotion> {
        return promotions.filter(this::isServerPromotionParseable)
            .map(ServerPromotion::toExternalModel)
    }

    /**
     * Checks whether a promotion coming from the server is parseable to any of the client-known
     * promotion types.
     *
     * In the case where the server returns a promotion that is not known by the client, we could
     * avoid the App crash for this reason and instead try "skip" this promotion for the product,
     * while at the same time we should:
     *
     * (1) If there's a new version of the App supporting the new promotions, we could suggest or
     *     force an update.
     * (2) If there's no an updated app version that supports the new promotion, then this should be
     *     treated as an unexpected parsing error and we should emit a metric or report to
     *     crashlytics to debug ahs fix this issue properly ASAP.
     */
    private fun isServerPromotionParseable(serverPromo: ServerPromotion): Boolean {
        return try {
            PromotionType.valueOf(serverPromo.type)
            true
        } catch (th: Throwable) {
            // TODO: Emit a metric here to visualise the parsing errors segmented by promotion type.
            Log.e(TAG, "Error while trying to parse promotion type '${serverPromo.type}'.", th)
            false
        }
    }

    companion object {
        private val TAG = DatabaseBackedPromotionRepository::class.simpleName
    }
}
