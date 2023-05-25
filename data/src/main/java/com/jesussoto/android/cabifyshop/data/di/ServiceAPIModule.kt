package com.jesussoto.android.cabifyshop.data.di

import com.google.gson.Gson
import com.jesussoto.android.cabibyshop.data.BuildConfig
import com.jesussoto.android.cabifyshop.data.api.CabifyShopServiceAPI
import com.jesussoto.android.cabifyshop.data.api.FakePromotionsDataSource
import com.jesussoto.android.cabifyshop.data.api.PromotionsDataSource
import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
internal class ServiceAPIModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): Call.Factory {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideService(okhttpCallFactory: Call.Factory): CabifyShopServiceAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVICE_ENDPOINT_URL)
            .callFactory(okhttpCallFactory)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(CabifyShopServiceAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesPromotionDataSource(): PromotionsDataSource {
        return FakePromotionsDataSource(Gson())
    }
}
