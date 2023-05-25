package com.jesussoto.android.cabifyshop.ui.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.data.model.asBaseCartItem
import com.jesussoto.android.cabifyshop.data.repository.CartRepository
import com.jesussoto.android.cabifyshop.ui.cart.CartUIAction.ShowSnackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    @RxScheduler(ShopSchedulers.Default) private val scheduler: Scheduler
): ViewModel() {

    private val disposables = CompositeDisposable()

    private val cart = BehaviorSubject.create<Cart>()

    private val uiAction = PublishSubject.create<CartUIAction>()

    init {
        // TODO: Move this fetch logic to an UseCase in the domain layer.
        disposables.add(cartRepository.getCart()
            .observeOn(scheduler)
            .subscribe(cart::onNext, this::handleCartError))
    }

    fun getCart(): Observable<Cart> = cart

    fun getUIAction(): Observable<CartUIAction> = uiAction

    fun closeAndGoBack() {
        uiAction.onNext(CartUIAction.CloseAndGoBack)
    }

    fun goToCheckout() {
        uiAction.onNext(ShowSnackbar(R.string.cart_checkout_not_implemented))
    }

    fun setItemQuantity(cartItem: ProcessedCartItem, newQuantity: Int) {
        disposables.add(cartRepository.updateItemQuantity(cartItem.asBaseCartItem(), newQuantity)
            .observeOn(scheduler)
            .subscribe(
                // OnComplete
                { uiAction.onNext(ShowSnackbar(R.string.cart_quantity_updated)) },
                // OnError
                { uiAction.onNext(ShowSnackbar(R.string.cart_update_quantity_failed)) }
            )
        )
    }

    private fun handleCartError(th: Throwable) {
        Log.e(TAG, "An error has occurred while fetching cart.", th)
        closeAndGoBack()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    companion object {
        private val TAG = CartViewModel::class.simpleName
    }
}
