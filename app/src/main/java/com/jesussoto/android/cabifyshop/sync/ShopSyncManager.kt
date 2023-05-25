package com.jesussoto.android.cabifyshop.sync

import android.content.Context
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.jesussoto.android.cabifyshop.data.di.qualifier.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {

    private val isSyncing = BehaviorSubject.createDefault(false)

    init {
        // This might seem a bit strange. the WorkManager API uses LiveData to report reactive job
        // status updates, and it doesn't have RxJava support. Therefore, since we're using Rx
        // patterns throughout the App, we're transforming the LiveData into an Observable by
        // caching the latest value in a BehaviorSubject.
        val workManager = WorkManager.getInstance(context)
        Transformations.map(
            workManager.getWorkInfosForUniqueWorkLiveData(ShopSyncWorker.WORK_NAME),
            MutableList<WorkInfo>::anyRunning
        ).observeForever(isSyncing::onNext)
    }

    override fun isSyncing(): Observable<Boolean> = isSyncing
        .distinctUntilChanged()
        .subscribeOn(Schedulers.computation())

    /**
     *  Executes sync on app startup (onCreate) and ensure only one expedited sync work runs at any time.
     */
    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)

        val operation = workManager.enqueueUniqueWork(
            ShopSyncWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            ShopSyncWorker.startUpSyncWork(),
        )

        operation.state
    }
}

private val List<WorkInfo>.anyRunning get() = any { it.state == WorkInfo.State.RUNNING }
