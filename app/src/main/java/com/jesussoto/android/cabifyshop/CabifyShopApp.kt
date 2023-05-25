package com.jesussoto.android.cabifyshop

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.jesussoto.android.cabifyshop.di.AppComponent
import com.jesussoto.android.cabifyshop.di.DaggerAppComponent
import com.jesussoto.android.cabifyshop.sync.ShopSyncWorker
import com.jesussoto.android.cabifyshop.sync.ShopWorkerFactory
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class CabifyShopApp: Application(), HasAndroidInjector, Configuration.Provider {

    @Inject
    lateinit var androidInjector : DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workerFactory: ShopWorkerFactory

    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        performInitialSync()
    }

    override fun androidInjector() = androidInjector

    override fun getWorkManagerConfiguration(): Configuration = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    private fun performInitialSync() {
        WorkManager.getInstance(this).run {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                ShopSyncWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                ShopSyncWorker.startUpSyncWork(),
            )
        }
    }
}
