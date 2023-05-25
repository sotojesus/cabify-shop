package com.jesussoto.android.cabifyshop.ui.cart.promotions

import androidx.lifecycle.ViewModel
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.Default
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

internal class PromotionsAppliedViewModel @Inject constructor(
    private val repository: CartRepository,
    @RxScheduler(Default) private val scheduler: Scheduler
): ViewModel() {

    private val disposables = CompositeDisposable()

    private val itemsWithPromotion = BehaviorSubject.createDefault<List<ProcessedCartItem>>(emptyList())

    init {
        // TODO: Move this fetch logic to an UseCase in the domain layer.
        disposables.add(repository.getCart()
            .map(Cart::discountedItems)
            .map(Set<ProcessedCartItem>::toList)
            .observeOn(scheduler)
            .subscribe(itemsWithPromotion::onNext) // TODO: Handle OnError.
        )
    }

    fun getItemsWithPromotion(): Observable<List<ProcessedCartItem>> = itemsWithPromotion

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
