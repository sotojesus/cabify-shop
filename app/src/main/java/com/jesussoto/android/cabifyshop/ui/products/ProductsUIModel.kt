package com.jesussoto.android.cabifyshop.ui.products

import com.jesussoto.android.cabifyshop.data.model.Product

sealed class ProductsUIModel(
    val products: List<Product>,
    val numberOfItemsInCart: Int = 0
) {

    class NoProducts : ProductsUIModel(emptyList())

    class DisplayProducts(products: List<Product>, numberOfItemsInCart: Int) :
        ProductsUIModel(products, numberOfItemsInCart)
}

sealed class ProductsUIAction {

    class NavigateToProductDetail(val product: Product): ProductsUIAction()
}
