package com.jesussoto.android.cabifyshop.ui.productdetail

import androidx.annotation.StringRes
import com.jesussoto.android.cabifyshop.data.model.Product

internal sealed class ProductDetailUIModel(
    val product: Product?,

    val hasPromotion: Boolean,

    val promotionTitleHtml: String? = null,

    val promotionDescriptionHtml: String? = null,

    val quantityInCart: Int = 0
) {
    class Idle : ProductDetailUIModel(null, false)

    class DisplayProduct(
        product: Product,
        hasPromotion: Boolean,
        promotionTitleHtml: String? = null,
        promotionDescriptionHtml: String? = null,
        quantityInCart: Int
    ) : ProductDetailUIModel(
        product,
        hasPromotion,
        promotionTitleHtml,
        promotionDescriptionHtml,
        quantityInCart
    )
}

internal sealed class ProductDetailUIAction {

    object CloseAndNavigateBack : ProductDetailUIAction()

    class ShowSnackBar(@StringRes val textResId: Int) : ProductDetailUIAction()

    class ShowToast(@StringRes val textResId: Int) : ProductDetailUIAction()
}
