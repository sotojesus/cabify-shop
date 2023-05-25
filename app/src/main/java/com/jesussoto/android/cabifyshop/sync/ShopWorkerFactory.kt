package com.jesussoto.android.cabifyshop.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopWorkerFactory @Inject constructor(
    private val syncWorkFactory: ShopSyncWorker.Factory,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {

        return when (workerClassName) {
            ShopSyncWorker::class.java.name ->
                syncWorkFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}
