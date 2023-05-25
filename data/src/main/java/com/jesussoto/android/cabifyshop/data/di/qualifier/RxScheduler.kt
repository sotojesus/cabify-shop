package com.jesussoto.android.cabifyshop.data.di.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RxScheduler(val scheduler: ShopSchedulers)

enum class ShopSchedulers {
    Default,
    IO
}
