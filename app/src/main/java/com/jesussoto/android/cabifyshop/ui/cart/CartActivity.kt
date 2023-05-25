package com.jesussoto.android.cabifyshop.ui.cart

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.databinding.ActivityCartBinding
import com.jesussoto.android.cabifyshop.di.ViewModelFactory
import com.jesussoto.android.cabifyshop.ui.base.BaseActivity
import com.jesussoto.android.cabifyshop.ui.cart.promotions.PromotionsAppliedSheet
import com.jesussoto.android.cabifyshop.ui.util.ResourcesProvider
import dagger.android.AndroidInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

internal class CartActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var resourcesProvider: ResourcesProvider

    private val viewModel: CartViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityCartBinding

    private lateinit var adapter: CartAdapter

    private lateinit var footerAdapter: CartTotalPriceFooterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        setupToolbar()
        setupRecyclerView()
        setupEmptyView()

        binding.checkoutButton.setOnClickListener { viewModel.goToCheckout() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { viewModel.closeAndGoBack() }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(viewModel::setItemQuantity)
        footerAdapter = CartTotalPriceFooterAdapter {
            PromotionsAppliedSheet().also {
                it.show(supportFragmentManager, PromotionsAppliedSheet.TAG)
            }
        }

        val compositeAdapter = ConcatAdapter(adapter, footerAdapter)
        with(binding.cartRecyclerView) {
            this.adapter = compositeAdapter
        }
    }

    private fun setupEmptyView() {
        with(binding.emptyView) {
            emptyTitleView.text = getString(R.string.cart_empty_title)
            emptyDescriptionView.text = getString(R.string.cart_empty_description)
            emptyAction.visibility = View.GONE
        }
    }

    override fun bindViewModel(disposables: CompositeDisposable) {
        disposables.add(viewModel.getCart()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateCart))

        disposables.add(viewModel.getUIAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::processAction))
    }

    private fun updateCart(cart: Cart) {
        adapter.submitList(cart.items)
        footerAdapter.replaceCart(cart)

        binding.cartRecyclerView.visibility = cart.items.isNotEmpty().toVisibleOrGone()
        binding.emptyView.emptyContainer.visibility = cart.items.isEmpty().toVisibleOrGone()

        binding.checkoutButton.visibility = cart.items.isNotEmpty().toVisibleOrGone()
        binding.checkoutButton.text = getString(R.string.cart_pay_button,
            NumberFormat.getCurrencyInstance().format(cart.price.totalDiscountedPrice),
            cart.totalItemQuantity
        )
    }

    private fun processAction(action: CartUIAction) {
        when (action) {
            CartUIAction.CloseAndGoBack -> navigateUp()
            is CartUIAction.ShowSnackbar -> showSnackbar(binding.root, action.textResId)
        }
    }

    companion object {
        fun start(launching: FragmentActivity) {
            launching.startActivity(Intent(launching, CartActivity::class.java))
        }
    }
}
