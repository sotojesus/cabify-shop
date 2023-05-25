package com.jesussoto.android.cabifyshop.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jesussoto.android.cabifyshop.ui.cart.CartViewModel
import com.jesussoto.android.cabifyshop.ui.cart.promotions.PromotionsAppliedViewModel
import com.jesussoto.android.cabifyshop.ui.productdetail.ProductDetailViewModel
import com.jesussoto.android.cabifyshop.ui.products.ProductsViewModel
import dagger.Binds
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider

@Module
internal abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

internal class ViewModelFactory @Inject constructor(
    productsViewModelProvider: Provider<ProductsViewModel>,
    productDetailViewModelProvider: Provider<ProductDetailViewModel>,
    cartViewModelProvider: Provider<CartViewModel>,
    promotionsAppliedViewModelProvider: Provider<PromotionsAppliedViewModel>,
) : ViewModelProvider.Factory {

    private val providers = mapOf(
        ProductsViewModel::class.java to productsViewModelProvider,
        ProductDetailViewModel::class.java to productDetailViewModelProvider,
        CartViewModel::class.java to cartViewModelProvider,
        PromotionsAppliedViewModel::class.java to promotionsAppliedViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel =  providers[modelClass]?.get()
            ?: throw IllegalArgumentException("Unknown ViewModel '$modelClass'. Did you forget to add it to the ViewModelFactory?")

        return viewModel as T
    }
}
