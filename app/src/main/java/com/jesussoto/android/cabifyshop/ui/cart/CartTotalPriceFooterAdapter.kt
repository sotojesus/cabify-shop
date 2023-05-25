package com.jesussoto.android.cabifyshop.ui.cart

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.Cart
import com.jesussoto.android.cabifyshop.databinding.ListFooterCartTotalPriceBinding
import com.jesussoto.android.cabifyshop.ui.util.applyStrikethroughSpan

internal class CartTotalPriceFooterAdapter(
    private val onClickPromotionsApplied: () -> Unit
): RecyclerView.Adapter<CartTotalPriceFooterHolder>() {

    private var cart: Cart? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartTotalPriceFooterHolder {
        return CartTotalPriceFooterHolder.create(parent, onClickPromotionsApplied)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: CartTotalPriceFooterHolder, position: Int) {
        cart?.also {
            holder.bindCart(it)
        }
    }

    fun replaceCart(cart: Cart?) {
        this.cart = cart
        notifyItemChanged(0)
    }
}

internal class CartTotalPriceFooterHolder private constructor(
    private val binding: ListFooterCartTotalPriceBinding,
    private val onClickPromotionsApplied: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    init {
        binding.promotionView.setOnClickListener { onClickPromotionsApplied() }
    }

    fun bindCart(cart: Cart) {
        val context = binding.root.context

        binding.promotionView.visibility = cart.discountedItems.isNotEmpty().toVisibleOrGone()

        binding.promotionsAppliedLabel.text = context.getString(
            R.string.cart_promotions_applied,
            cart.discountedItems.size)

        binding.promotionsAmountAppliedView.text = context.getString(
            R.string.cart_promotions_applied_savings,
            currencyFormatter.format(cart.totalSavingsAmount),
            cart.totalSavingsPercentage)

        binding.totalDiscountedPriceView.text =
            currencyFormatter.format(cart.price.totalDiscountedPrice)

        binding.totalPriceWithoutDiscountView.visibility =
            cart.discountedItems.isNotEmpty().toVisibleOrGone()

        binding.totalPriceWithoutDiscountView.text = applyStrikethroughSpan(
            currencyFormatter.format(cart.price.totalPrice))
    }

    companion object {
        fun create(parent: ViewGroup, onClickPromotionsApplied: () -> Unit): CartTotalPriceFooterHolder {
            val inflater = LayoutInflater.from(parent.context)
            return CartTotalPriceFooterHolder(
                ListFooterCartTotalPriceBinding.inflate(inflater, parent, false),
                onClickPromotionsApplied
            )
        }
    }
}
