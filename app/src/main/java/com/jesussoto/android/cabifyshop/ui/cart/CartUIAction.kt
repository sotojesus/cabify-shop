package com.jesussoto.android.cabifyshop.ui.cart

import androidx.annotation.StringRes

sealed class CartUIAction {

    object CloseAndGoBack: CartUIAction()

    class ShowSnackbar(@StringRes val textResId: Int): CartUIAction()
}
