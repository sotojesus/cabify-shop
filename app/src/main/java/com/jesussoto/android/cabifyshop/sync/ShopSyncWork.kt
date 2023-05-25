package com.jesussoto.android.cabifyshop.sync

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import com.jesussoto.android.cabifyshop.data.repository.ProductRepository
import com.jesussoto.android.cabifyshop.data.repository.PromotionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Collections

class ShopSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val cartRepository: CartRepository
): RxWorker(appContext, params) {

    /**
     * Sync logic is very simple, it basically fetches from server and replace the data in the
     * local database while trying to keep the cart items.
     *
     * Ideally, we might try an more transactional approach to avoid the database hanging in an
     * undesired state if any network error occur during the syncing process. But since the DB
     * has ForeignKeys on products keys, there's no possibility of ending up with promotions or
     * cart items of stale product data.
     */
    override fun createWork(): Single<Result> {
        // This list serves as a backup to prevent loosing the cart items during a full sync
        // from server, which is used to restore the cart items upon successful sync.
        val backupItems = Collections.synchronizedList(mutableListOf<CartItem>())

        return cartRepository.getCartItems()
            .take(1)
            .singleOrError()
            .doOnSuccess { backupItems.addAll(it) }
            .flatMapCompletable { productRepository.sync() }
            .andThen(promotionRepository.sync())
            .andThen(cartRepository.replaceCartItems(backupItems).onErrorComplete())
            .doOnError(this::handleError)
            .andThen(Single.just(Result.success()))
            .onErrorReturnItem(Result.failure())
    }

    /**
     * This error handling is simple and minimal. We could also configure a retry and backoff
     * logic to re-attempt syncing in case of failures
     */
    private fun handleError(th: Throwable) {
        Log.e(TAG, "An error has occurred during sync", th)

        // This tiny one-off stream is just to schedule a toast in the MainThread.
        // We're not interested on handling error only for showing a toast in this situation.
        Completable.fromAction {
            Toast.makeText(appContext, appContext.getString(R.string.sync_error),
                Toast.LENGTH_LONG).show()
        }
            .subscribeOn(AndroidSchedulers.mainThread())
            .onErrorComplete()
            .subscribe()
    }

    @AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): ShopSyncWorker
    }

    companion object {
        private val TAG = ShopSyncWorker::class.simpleName

        internal const val WORK_NAME = "ShopSyncWork"

        private val SYNC_CONSTRAINTS
            get() = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<ShopSyncWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SYNC_CONSTRAINTS)
            .build()
    }
}
