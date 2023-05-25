package com.jesussoto.android.cabifyshop.data.di

import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.Default
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.IO
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

@Module
internal class SchedulersModule {

    @Singleton
    @Provides
    @RxScheduler(Default)
    fun provideDefaultScheduler(): Scheduler {
        return Schedulers.computation()
    }

    @Singleton
    @Provides
    @RxScheduler(IO)
    fun provideIOScheduler(): Scheduler {
        return Schedulers.io()
    }
}
