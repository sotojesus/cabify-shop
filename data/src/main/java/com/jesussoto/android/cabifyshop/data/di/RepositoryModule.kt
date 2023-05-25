package com.jesussoto.android.cabifyshop.data.di

import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import com.jesussoto.android.cabifyshop.data.repository.DatabaseBackedCartRepository
import com.jesussoto.android.cabifyshop.data.repository.DatabaseBackedProductRepository
import com.jesussoto.android.cabifyshop.data.repository.DatabaseBackedPromotionRepository
import com.jesussoto.android.cabifyshop.data.repository.ProductRepository
import com.jesussoto.android.cabifyshop.data.repository.PromotionRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindProductRepository(repository: DatabaseBackedProductRepository): ProductRepository

    @Singleton
    @Binds
    abstract fun bindPromotionRepository(repository: DatabaseBackedPromotionRepository): PromotionRepository

    @Singleton
    @Binds
    abstract fun bindCartRepository(repository: DatabaseBackedCartRepository): CartRepository
}
