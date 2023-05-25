package com.jesussoto.android.cabifyshop.ui.productdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.Default
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import com.jesussoto.android.cabifyshop.data.repository.ProductRepository
import com.jesussoto.android.cabifyshop.ui.cart.promotions.getDescriptionForPromotion
import com.jesussoto.android.cabifyshop.ui.cart.promotions.getTitleForPromotion
import com.jesussoto.android.cabifyshop.ui.productdetail.ProductDetailUIAction.ShowSnackBar
import com.jesussoto.android.cabifyshop.ui.util.ResourcesProvider
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.Objects
import javax.inject.Inject

class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val resourcesProvider: ResourcesProvider,
    @RxScheduler(Default) private val scheduler: Scheduler
): ViewModel() {

    private lateinit var productCode: String

    private lateinit var product: Product

    private val productCodeSubject = BehaviorSubject.create<String>()

    private val uiModel = BehaviorSubject.createDefault<ProductDetailUIModel>(ProductDetailUIModel.Idle())

    private val uiAction = PublishSubject.create<ProductDetailUIAction>()

    private val disposables = CompositeDisposable()

    init {
        // TODO: Move this fetch and combine logic to an UseCase in the domain layer.
        disposables.add(productCodeSubject.toFlowable(BackpressureStrategy.LATEST)
            .filter(Objects::nonNull)
            .switchMap(this::getProduct)
            .switchMap(this::getQuantityInCartForProduct)
            .map(this::buildUIModel)
            .observeOn(scheduler)
            .subscribe(
                // OnNext
                uiModel::onNext,
                // OnError
                this::handleProductError
            )
        )
    }

    private fun getProduct(productCode: String): Flowable<Product> {
        return productRepository.getProduct(productCode)
            .doOnNext { this.product = it}
    }

    private fun getQuantityInCartForProduct(product: Product): Flowable<Pair<Product, Int>> {
        return cartRepository.getCartItemForProduct(product)
            .map(CartItem::quantity)
            .startWithItem(0)
            .map { Pair(product, it) }
    }

    // Ideally the productCode should be passed in as constructor parameter injecting it
    // using Dagger's assisted injection.
    internal fun setProductCode(productCode: String) {
        Log.d(TAG, "productCode: $productCode")
        this.productCode = productCode
        this.productCodeSubject.onNext(productCode)
    }

    internal fun navigateBack() {
        uiAction.onNext(ProductDetailUIAction.CloseAndNavigateBack)
    }

    internal fun getUIModel() : Observable<ProductDetailUIModel> = uiModel

    internal fun getUIAction() : Observable<ProductDetailUIAction> = uiAction

    internal fun addToCart() {
        disposables.add(cartRepository.addOneToCart(product)
            .observeOn(scheduler)
            .subscribe(
                // OnComplete
                { uiAction.onNext(ShowSnackBar(R.string.product_detail_added_to_cart)) },
                // onError
                { uiAction.onNext(ShowSnackBar(R.string.product_detail_add_to_cart_failed)) }
            )
        )
    }

    private fun buildUIModel(productWithQuantity: Pair<Product, Int>): ProductDetailUIModel {
        val product = productWithQuantity.first
        val hasPromotion = product.promotion != null
        var promoTitle: String? = null
        var promoDescription: String? = null

        product.promotion?.run {
            promoTitle = getTitleForPromotion(product, resourcesProvider)
            promoDescription = getDescriptionForPromotion(product, resourcesProvider)
        }

        return ProductDetailUIModel.DisplayProduct(
            product, hasPromotion, promoTitle, promoDescription, productWithQuantity.second)
    }

    private fun handleProductError(th: Throwable) {
        Log.d(TAG, "Error while loading product=$productCode", th)
        uiAction.onNext(ProductDetailUIAction.ShowToast(R.string.product_detail_error_loading_product))
        uiAction.onNext(ProductDetailUIAction.CloseAndNavigateBack)
    }

    companion object {
        private val TAG = ProductDetailViewModel::class.java.simpleName
    }
}
