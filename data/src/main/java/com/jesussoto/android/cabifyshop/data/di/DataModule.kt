package com.jesussoto.android.cabifyshop.data.di

import dagger.Module

@Module(
    includes = [
        SchedulersModule::class,
        DatabaseModule::class,
        ServiceAPIModule::class,
        RepositoryModule::class
    ]
)
class DataModule
