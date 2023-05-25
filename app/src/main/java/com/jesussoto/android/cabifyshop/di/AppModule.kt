package com.jesussoto.android.cabifyshop.di

import android.app.Application
import android.content.Context
import com.jesussoto.android.cabifyshop.data.di.qualifier.ApplicationContext
import com.jesussoto.android.cabifyshop.ui.util.AndroidResourcesProvider
import com.jesussoto.android.cabifyshop.ui.util.ResourcesProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    @ApplicationContext
    fun provideApplicationContext(app: Application): Context {
        return app.applicationContext
    }

    @Singleton
    @Provides
    fun bindResourceProvider(provider: AndroidResourcesProvider): ResourcesProvider {
        return provider
    }
}
