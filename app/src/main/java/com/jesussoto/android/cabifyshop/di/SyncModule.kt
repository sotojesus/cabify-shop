package com.jesussoto.android.cabifyshop.di

import com.jesussoto.android.cabifyshop.sync.ShopSyncManager
import com.jesussoto.android.cabifyshop.sync.SyncManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SyncModule {

    @Singleton
    @Binds
    abstract fun provideApplicationContext(syncManager: ShopSyncManager): SyncManager
}
