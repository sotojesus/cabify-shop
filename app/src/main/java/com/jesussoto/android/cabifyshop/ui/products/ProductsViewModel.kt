package com.jesussoto.android.cabifyshop.ui.products

import androidx.lifecycle.ViewModel
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import com.jesussoto.android.cabifyshop.data.repository.ProductRepository
import com.jesussoto.android.cabifyshop.sync.SyncManager
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val syncManager: SyncManager,
    @RxScheduler(ShopSchedulers.Default) private val scheduler: Scheduler
): ViewModel() {

    private val disposables = CompositeDisposable()

    private val uiModel = BehaviorSubject.createDefault<ProductsUIModel>(ProductsUIModel.NoProducts())

    private val uiAction = PublishSubject.create<ProductsUIAction>()

    init {
        // TODO: Move this fetch and combine logic to an UseCase in the domain layer.
        disposables.add(
            Flowable.combineLatest(
                productRepository.getProducts(),
                cartRepository.getCart(),
                this::buildUIModel
            )
            .doOnSubscribe { uiModel.onNext(ProductsUIModel.NoProducts()) }
            .subscribeOn(scheduler)
            .subscribe(uiModel::onNext))
    }

    fun refreshProducts() {
        syncManager.requestSync()
    }

    fun navigateToProductDetail(product: Product) {
        uiAction.onNext(ProductsUIAction.NavigateToProductDetail(product))
    }

    fun getUIModel(): Observable<ProductsUIModel> = uiModel

    fun getUIAction(): Observable<ProductsUIAction> = uiAction

    fun getIsSyncing(): Observable<Boolean> = syncManager.isSyncing()
        .throttleLast(1, TimeUnit.SECONDS)

    private fun buildUIModel(products: List<Product>, cart: Cart): ProductsUIModel {
        return if (products.isEmpty()) ProductsUIModel.NoProducts()
        else ProductsUIModel.DisplayProducts(products, cart.totalItemQuantity)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
