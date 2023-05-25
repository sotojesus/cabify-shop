package com.jesussoto.android.cabifyshop.di

import android.app.Application
import com.jesussoto.android.cabifyshop.CabifyShopApp
import com.jesussoto.android.cabifyshop.data.di.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBindingModule::class,
        FragmentBindingModule::class,
        SyncModule::class,
        AppModule::class,
        DataModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: CabifyShopApp)
}
