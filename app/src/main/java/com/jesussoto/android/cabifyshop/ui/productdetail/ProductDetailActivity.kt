package com.jesussoto.android.cabifyshop.ui.productdetail

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.databinding.ActivityProductDetailBinding
import com.jesussoto.android.cabifyshop.di.ViewModelFactory
import com.jesussoto.android.cabifyshop.ui.base.BaseActivity
import com.jesussoto.android.cabifyshop.ui.cart.toVisibleOrGone
import com.jesussoto.android.cabifyshop.ui.util.getImageBitmapForProduct
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

internal class ProductDetailActivity: BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProductDetailViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(EXTRA_PRODUCT_CODE)) {
            makeToast(R.string.product_detail_missing_product_code)
            Log.e(TAG,"This activity requires a product code, but it was missing")
            finish()
            return
        }

        binding = ActivityProductDetailBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val productCode = intent.getStringExtra(EXTRA_PRODUCT_CODE)!!
        viewModel.setProductCode(productCode)

        setupToolbar()

        binding.addToCartButton.setOnClickListener {
            viewModel.addToCart()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null
        binding.toolbar.setNavigationOnClickListener { viewModel.navigateBack() }
    }

    override fun bindViewModel(disposables: CompositeDisposable) {
        disposables.add(viewModel.getUIModel()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateView))

        disposables.add(viewModel.getUIAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::processAction))
    }

    private fun updateView(uiModel: ProductDetailUIModel) {
        binding.productTitleView.text = uiModel.product?.name
        binding.productPriceView.text = uiModel.product?.let {
            NumberFormat.getCurrencyInstance().format(it.price)
        }

        binding.promotionView.visibility = uiModel.hasPromotion.toVisibleOrGone()

        uiModel.promotionTitleHtml?.run {
            binding.promotionTitleView.text = HtmlCompat.fromHtml(
                this, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

        uiModel.promotionDescriptionHtml?.run {
            binding.promotionDescriptionView.text = HtmlCompat.fromHtml(
                this, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

        uiModel.product?.code?.run {
            Glide.with(this@ProductDetailActivity)
                .load(getImageBitmapForProduct(this, this@ProductDetailActivity))
                .placeholder(R.drawable.image_placeholder)
                .into(binding.productImage)
        }

        binding.addToCartButton.text = if (uiModel.quantityInCart > 0) {
            getString(R.string.product_detail_fab_add_to_cart_with_items, uiModel.quantityInCart)
        } else {
            getString(R.string.product_detail_fab_add_to_cart_no_items)
        }
    }

    private fun processAction(action: ProductDetailUIAction) {
        when (action) {
            ProductDetailUIAction.CloseAndNavigateBack -> navigateUp()
            is ProductDetailUIAction.ShowSnackBar -> showSnackbar(binding.root, action.textResId)
            is ProductDetailUIAction.ShowToast -> makeToast(action.textResId)
        }
    }

    companion object {
        private val TAG = ProductDetailActivity::class.simpleName

        private const val EXTRA_PRODUCT_CODE = "extra_product_code"

        fun start(launching: FragmentActivity, product: Product) {
            val startIntent = Intent(launching, ProductDetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_CODE, product.code)
            }
            launching.startActivity(startIntent)
        }
    }
}
