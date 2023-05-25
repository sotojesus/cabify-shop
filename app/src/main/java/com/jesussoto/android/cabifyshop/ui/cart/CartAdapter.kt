package com.jesussoto.android.cabifyshop.ui.cart

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.databinding.ListItemCartItemBinding
import com.jesussoto.android.cabifyshop.ui.util.applyStrikethroughSpan
import com.jesussoto.android.cabifyshop.ui.util.getImageBitmapForProduct
import java.text.NumberFormat

internal class CartAdapter(
    private val onItemQuantityChanged: (cartItem: ProcessedCartItem, newQuantity: Int) -> Unit
): ListAdapter<ProcessedCartItem, CartViewHolder>(CartItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder.create(parent, onItemQuantityChanged)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bindCartItem(getItem(position))
    }
}

internal class CartViewHolder private constructor(
    private val binding: ListItemCartItemBinding,
    private val onItemQuantityChanged: (cartItem: ProcessedCartItem, newQuantity: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var cartItem: ProcessedCartItem? = null

    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    init {
        binding.productQuantityView.setOnClickListener {
            showQuantitySelectionMenu(it, R.menu.menu_item_quantity)
        }
    }

    fun bindCartItem(cartItem: ProcessedCartItem) {
        this.cartItem = cartItem
        val context = binding.root.context

        binding.productTitleView.text = cartItem.product.name

        binding.productQuantityView.text = context.getString(
            R.string.cart_item_quantity, cartItem.quantity)

        binding.productDiscountedPriceView.text =
            currencyFormatter.format(cartItem.price.totalDiscountedPrice)

        binding.productPriceWithoutDiscountView.visibility =
            (cartItem.price.appliedPromotion != null).toVisibleOrGone()

        binding.productPriceWithoutDiscountView.text = applyStrikethroughSpan(
            currencyFormatter.format(cartItem.price.totalPrice))

        Glide.with(context)
            .load(getImageBitmapForProduct(cartItem.product.code, context))
            .placeholder(R.drawable.image_placeholder)
            .into(binding.productThumbnail)
    }

    private fun showQuantitySelectionMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // TODO: Report menu item not found to crashlytics.
            val newQuantity = QUANTITY_BY_MENU_ITEM_ID[menuItem.itemId]
                ?: return@setOnMenuItemClickListener false

            cartItem?.run {
                onItemQuantityChanged(this, newQuantity)
            }

            true
        }

        popup.show()
    }

    companion object {
        fun create(parent: ViewGroup, onItemQuantityChanged: (cartItem: ProcessedCartItem, newQuantity: Int) -> Unit): CartViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return CartViewHolder(
                ListItemCartItemBinding.inflate(inflater, parent, false),
                onItemQuantityChanged
            )
        }

        private val QUANTITY_BY_MENU_ITEM_ID = mapOf(
            R.id.item_quantity_remove to 0,
            R.id.item_quantity_1 to 1,
            R.id.item_quantity_2 to 2,
            R.id.item_quantity_3 to 3,
            R.id.item_quantity_4 to 4,
            R.id.item_quantity_5 to 5,
            R.id.item_quantity_6 to 6,
            R.id.item_quantity_7 to 7,
            R.id.item_quantity_8 to 8,
            R.id.item_quantity_9 to 9,
        )
    }
}

internal object CartItemDiffCallback : DiffUtil.ItemCallback<ProcessedCartItem>() {

    override fun areItemsTheSame(oldItem: ProcessedCartItem, newItem: ProcessedCartItem): Boolean {
        return oldItem.product.code == newItem.product.code
    }

    override fun areContentsTheSame(oldItem: ProcessedCartItem, newItem: ProcessedCartItem): Boolean {
        return oldItem == newItem
    }
}

// TODO: Move to Util
fun Boolean.toVisibleOrGone(): Int {
    return if (this) View.VISIBLE else View.GONE
}
