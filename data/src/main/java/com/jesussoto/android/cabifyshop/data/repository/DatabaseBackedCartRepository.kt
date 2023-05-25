package com.jesussoto.android.cabifyshop.data.repository

import android.util.Log
import com.jesussoto.android.cabifyshop.data.cart.CartPriceCalculator
import com.jesussoto.android.cabifyshop.data.database.dao.CartDao
import com.jesussoto.android.cabifyshop.data.database.entity.CartItemEntity
import com.jesussoto.android.cabifyshop.data.database.entity.CartItemWithProduct
import com.jesussoto.android.cabifyshop.data.database.entity.toExternalModel
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.IO
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.data.model.CartItem
import com.jesussoto.android.cabifyshop.data.model.Product
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Given ths scope of the project, all operations in this class are performed in a simple,
 * self-contained approach.
 *
 * In a production shopping App, adding to cart should be more elaborated if there's need to
 * check for inventory availability or propagate changes to the backend first to ensure
 * STRONG CONSISTENCY.
 */
class DatabaseBackedCartRepository @Inject internal constructor(
    private val cartDao: CartDao,
    private val priceCalculator: CartPriceCalculator,
    @RxScheduler(IO) private val scheduler: Scheduler
): CartRepository {

    /**
     * For simplicity, I'm just adding one to the quantity every time this method is called.
     * In a production shopping App, adding to cart should be more elaborated if there's need to
     * check for inventory availability or propagate changes to the backend first.
     */
    override fun addOneToCart(product: Product): Completable {
        return Completable.fromAction { internalAddOneToCart(product) }
            .doOnError {
                Log.e(TAG, "Error while adding product=${product.code} to cart", it)
                // TODO: report the crash to crashlytics so we have visibility over it.
            }
            .subscribeOn(scheduler)
    }

    @Throws
    private fun internalAddOneToCart(product: Product) {
        val currentCartItem = cartDao.getCartItemForProductSynchronous(product.code)

        // For simplicity, remove the whole cart item if we receive a product.
        // In a Prod App, this error handling must be a business decision discussed carefully.
        if (currentCartItem != null && currentCartItem.quantity <= 0) {
            // TODO: Emit a metric here to track if this error ever happens.
            cartDao.deleteSynchronous(currentCartItem)
            throw CartException.IntegrityException("Error adding product=${product.code} to cart. " +
                    "There was an item already in cart with invalid quantity=${currentCartItem.quantity}. " +
                    "The entire item has been removed from cart as a fallback.")
        }

        if (currentCartItem != null)
            internalUpdateItemQuantity(currentCartItem, currentCartItem.quantity + 1)
        else {
            cartDao.insertSynchronous(CartItemEntity(product.code, 1))
        }
    }

    override fun updateItemQuantity(cartItem: CartItem, newQuantity: Int): Completable {
        return Completable.fromAction { internalUpdateItemQuantity(CartItemEntity(cartItem), newQuantity) }
            .doOnError {
                Log.e(TAG, "Error while updating quantity for item=$cartItem", it)
                // TODO: report the crash to crashlytics so we have visibility over it.
            }
            .subscribeOn(scheduler)
    }

    @Throws
    private fun internalUpdateItemQuantity(cartItemEntity: CartItemEntity, newQuantity: Int) {
        // If current and new quantities are the same, nothing to do.
        if (cartItemEntity.quantity == newQuantity) {
            return
        }

        if (newQuantity > 0) {
            cartDao.updateSynchronous(CartItemEntity(cartItemEntity.productCode, newQuantity))
        } else {
            cartDao.deleteSynchronous(cartItemEntity)
        }
    }

    override fun getCartItems(): Flowable<List<CartItem>> {
        return cartDao.getCartItemsWithProduct()
            .map { it.map(CartItemWithProduct::toExternalModel) }
            .subscribeOn(scheduler)
    }

    override fun getCart(): Flowable<Cart> {
        return cartDao.getCartItemsWithProduct()
            .map { it.map(CartItemWithProduct::toExternalModel) }
            .map(priceCalculator::calculateTotalPrice)
            .subscribeOn(scheduler)
    }

    override fun getCartItemForProduct(product: Product): Flowable<CartItem> {
        return cartDao.getCartItemForProduct(product.code)
            .map(CartItemWithProduct::toExternalModel)
            .subscribeOn(scheduler)
    }

    override fun replaceCartItems(newItems: List<CartItem>): Completable {
        return cartDao.clearTable()
            .andThen(Single.just(newItems)
                .map { it.map(::CartItemEntity) }
                // TODO: Careful here, we could get ForeignKey violations exception of the backed-up
                //  cart item products are no longer available after a refresh
                // Callers may want to use onErrorComplete() to avoid ForeignKey violations
                // to report an error downstream if required.
                .flatMapCompletable(cartDao::insertAll))
            .subscribeOn(scheduler)
    }

    companion object {
        private val TAG = DatabaseBackedCartRepository::class.simpleName
    }
}

sealed class CartException(message: String, cause: Throwable? = null): Exception(message, cause) {

    class IntegrityException(message: String, cause: Throwable? = null) :
        CartException(message, cause)
}
