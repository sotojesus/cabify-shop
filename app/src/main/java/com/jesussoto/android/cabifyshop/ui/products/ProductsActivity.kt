package com.jesussoto.android.cabifyshop.ui.products

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.databinding.ActivityProductsBinding
import com.jesussoto.android.cabifyshop.di.ViewModelFactory
import com.jesussoto.android.cabifyshop.ui.base.BaseActivity
import com.jesussoto.android.cabifyshop.ui.cart.CartActivity
import com.jesussoto.android.cabifyshop.ui.cart.toVisibleOrGone
import com.jesussoto.android.cabifyshop.ui.productdetail.ProductDetailActivity
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

internal class ProductsActivity: BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ProductsViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityProductsBinding

    private lateinit var adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setupRecyclerView()
        setupEmptyView()
        setupRefreshLayout()

        binding.goToCartButton.setOnClickListener {
            CartActivity.start(this)
        }
    }

    override fun bindViewModel(disposables: CompositeDisposable) {
        disposables.add(viewModel.getUIModel()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateView))

        disposables.add(viewModel.getIsSyncing()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d("ProductsActivity", "isSyncing: $it")
                binding.swipeRefreshLayout.isRefreshing = it
            })

        disposables.add(viewModel.getUIAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::processAction))
    }

    private fun setupRecyclerView() {
        adapter = ProductsAdapter(viewModel::navigateToProductDetail)

        // The layout manager is being assigned directly in the XML definition.
        with(binding.productsRecyclerView) {
            this.adapter = this@ProductsActivity.adapter
        }
    }

    private fun setupEmptyView() {
        with(binding.emptyView) {
            this.emptyTitleView.text = getString(R.string.products_empty_title)
            this.emptyDescriptionView.text = getString(R.string.products_empty_description)
            this.emptyAction.text = getString(R.string.products_refresh)
            this.emptyAction.setOnClickListener { viewModel.refreshProducts() }
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setProgressViewEndTarget(
            true, binding.swipeRefreshLayout.progressViewEndOffset)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshProducts()
        }
    }

    private fun updateView(uiModel: ProductsUIModel) {
        adapter.submitList(uiModel.products)

        binding.emptyView.emptyContainer.visibility = uiModel.products.isEmpty().toVisibleOrGone()

        binding.goToCartButton.visibility = uiModel.products.isNotEmpty().toVisibleOrGone()

        binding.goToCartButton.text = if (uiModel.numberOfItemsInCart > 0) {
            getString(R.string.products_go_to_cart_with_items, uiModel.numberOfItemsInCart)
        } else {
            getString(R.string.products_go_to_cart_no_items)
        }
    }

    private fun processAction(action: ProductsUIAction) {
        when (action) {
            is ProductsUIAction.NavigateToProductDetail -> navigateToProductDetails(action.product)
        }
    }

    private fun navigateToProductDetails(product: Product) {
        ProductDetailActivity.start(this, product)
    }
}
