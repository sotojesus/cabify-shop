package com.jesussoto.android.cabifyshop.di

import com.jesussoto.android.cabifyshop.di.scope.ActivityScope
import com.jesussoto.android.cabifyshop.ui.cart.CartActivity
import com.jesussoto.android.cabifyshop.ui.productdetail.ProductDetailActivity
import com.jesussoto.android.cabifyshop.ui.products.ProductsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeProductsActivity(): ProductsActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeProductDetailActivity(): ProductDetailActivity?

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeCartActivity(): CartActivity
}