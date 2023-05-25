package com.jesussoto.android.cabifyshop.data.repository

import com.jesussoto.android.cabifyshop.data.api.CabifyShopServiceAPI
import com.jesussoto.android.cabifyshop.data.api.GetProductsResponse
import com.jesussoto.android.cabifyshop.data.api.ServerProduct
import com.jesussoto.android.cabifyshop.data.api.toExternalModel
import com.jesussoto.android.cabifyshop.data.database.dao.ProductDao
import com.jesussoto.android.cabifyshop.data.database.entity.ProductEntity
import com.jesussoto.android.cabifyshop.data.database.entity.ProductWithPromotions
import com.jesussoto.android.cabifyshop.data.database.entity.toExternalModel
import com.jesussoto.android.cabifyshop.data.di.qualifier.RxScheduler
import com.jesussoto.android.cabifyshop.data.di.qualifier.ShopSchedulers.IO
import com.jesussoto.android.cabifyshop.data.model.Product
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

internal class DatabaseBackedProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val service: CabifyShopServiceAPI,
    @RxScheduler(IO) private val scheduler: Scheduler
) : ProductRepository {

    override fun getProducts(): Flowable<List<Product>> {
        return productDao.getProductsWithPromotions()
            .map { it.map(ProductWithPromotions::toExternalModel) }
            .distinctUntilChanged()
            .subscribeOn(scheduler)
    }

    override fun getProduct(productCode: String): Flowable<Product> {
        return productDao.getProductWithPromotions(productCode)
            .distinctUntilChanged()
            .map(ProductWithPromotions::toExternalModel)
            .subscribeOn(scheduler)
    }

    override fun sync(): Completable {
        return service.getProducts()
            .map(GetProductsResponse::products)
            .map { it.map(ServerProduct::toExternalModel) }
            .map { it.map(::ProductEntity) }
            .flatMapCompletable(this::replaceProducts)
    }

    private fun replaceProducts(newProducts: List<ProductEntity>): Completable {
        return productDao.clearTable()
            .andThen(productDao.insertAll(newProducts))
    }
}
