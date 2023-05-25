package com.jesussoto.android.cabifyshop.di

import com.jesussoto.android.cabifyshop.di.scope.FragmentScope
import com.jesussoto.android.cabifyshop.ui.cart.promotions.PromotionsAppliedSheet
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentBindingModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributePromotionsSheetFragment(): PromotionsAppliedSheet
}
