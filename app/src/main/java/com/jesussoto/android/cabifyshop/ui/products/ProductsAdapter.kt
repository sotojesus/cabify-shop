package com.jesussoto.android.cabifyshop.ui.products

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.databinding.ListItemProductBinding
import com.jesussoto.android.cabifyshop.ui.cart.toVisibleOrGone
import com.jesussoto.android.cabifyshop.ui.util.getImageBitmapForProduct

internal class ProductsAdapter(
    private val onProductTapped: (Product) -> Unit
): ListAdapter<Product, ProductsViewHolder>(ProductDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder.create(parent, onProductTapped)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bindProduct(getItem(position))
    }
}

internal class ProductsViewHolder(
    private val binding: ListItemProductBinding,
    onProductTapped: (Product) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    private var product: Product? = null

    init {
        binding.root.setOnClickListener {
            product?.let(onProductTapped)
        }
    }

    fun bindProduct(product: Product) {
        this.product = product

        binding.productTitleView.text = product.name
        binding.productPriceView.text = currencyFormatter.format(product.price)
        binding.promotionIcon.visibility = (product.promotion != null).toVisibleOrGone()

        Glide.with(binding.root)
            .load(getImageBitmapForProduct(product.code, binding.root.context))
            .placeholder(R.drawable.image_placeholder)
            .into(binding.productThumbnail)
    }

    companion object {
        fun create(parent: ViewGroup, onProductTapped: (Product) -> Unit): ProductsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ProductsViewHolder(
                ListItemProductBinding.inflate(inflater, parent, false),
                onProductTapped
            )
        }
    }
}

internal object ProductDiffCallback : DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
