package com.jesussoto.android.cabifyshop.ui.cart.promotions

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.cart.BuyXGetXPromotionConfig
import com.jesussoto.android.cabifyshop.data.cart.FixedBulkPricePromotionConfig
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.data.model.Product
import com.jesussoto.android.cabifyshop.data.model.PromotionType
import com.jesussoto.android.cabifyshop.data.model.typedConfig
import com.jesussoto.android.cabifyshop.databinding.ListItemPromotionAppliedBinding
import com.jesussoto.android.cabifyshop.ui.cart.CartItemDiffCallback
import com.jesussoto.android.cabifyshop.ui.util.ResourcesProvider
import kotlin.math.abs

internal class PromotionsAppliedAdapter(
    private val resourcesProvider: ResourcesProvider
): ListAdapter<ProcessedCartItem, PromotionsAppliedViewHolder>(CartItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionsAppliedViewHolder {
        return PromotionsAppliedViewHolder.create(parent, resourcesProvider)
    }

    override fun onBindViewHolder(holder: PromotionsAppliedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

internal class PromotionsAppliedViewHolder private constructor(
    private val binding: ListItemPromotionAppliedBinding,
    private val resourcesProvider: ResourcesProvider
): RecyclerView.ViewHolder(binding.root) {

    private val currencyFormatter = NumberFormat.getCurrencyInstance()

    fun bind(itemWithPromotion: ProcessedCartItem) {
        if (itemWithPromotion.price.appliedPromotion == null) {
            // This should never happen as this point, since promotions here have been already
            // filtered to retrieve only applied ones.
            // To avoid crashing the App in a case like this, we'll return and log a metric.
            // TODO: Emit a metric to track if this ever happens.
            return
        }

        val title = getTitleForPromotion(itemWithPromotion.product, resourcesProvider)

        val description = getDescriptionForPromotion(itemWithPromotion.product, resourcesProvider)

        val savings = binding.root.context.getString(R.string.promotion_applied_savings,
            currencyFormatter.format(itemWithPromotion.price.totalSavingsAmount))

        binding.titleView.text = HtmlCompat.fromHtml(title, FROM_HTML_MODE_COMPACT)
        binding.descriptionView.text = HtmlCompat.fromHtml(description, FROM_HTML_MODE_COMPACT)
        binding.savingsView.text = savings
    }

    companion object {
        fun create(parent: ViewGroup, resourcesProvider: ResourcesProvider): PromotionsAppliedViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return PromotionsAppliedViewHolder(
                ListItemPromotionAppliedBinding.inflate(inflater, parent, false),
                resourcesProvider
            )
        }
    }
}

internal fun getTitleForPromotion(
    product: Product,
    resourcesProvider: ResourcesProvider,
    currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance()
): String {
    val promotion = product.promotion ?: return ""

    return when (promotion.type) {
        PromotionType.FixedBulkPricePromotion -> {
            val config = promotion.typedConfig<FixedBulkPricePromotionConfig>()
            resourcesProvider.getString(R.string.promotion_fixed_bulk_price_title,
                currencyFormatter.format(config.discountedUnitPrice),
                currencyFormatter.format(product.price))
        }
        PromotionType.BuyXGetXPromotion -> {
            val config = promotion.typedConfig<BuyXGetXPromotionConfig>()
            resourcesProvider.getString(R.string.promotion_buy_x_get_x_title,
                abs(config.minApplicableQuantity - config.quantityToGetForFree),
                config.quantityToGetForFree)
        }
    }
}

internal fun getDescriptionForPromotion(
    product: Product, resourcesProvider: ResourcesProvider
): String {
    val promotion = product.promotion ?: return ""

    return when (promotion.type) {
        PromotionType.FixedBulkPricePromotion -> {
            val config = promotion.typedConfig<FixedBulkPricePromotionConfig>()
            resourcesProvider.getString(R.string.promotion_fixed_bulk_price_description,
                product.name,
                config.minApplicableQuantity)
        }
        PromotionType.BuyXGetXPromotion -> {
            val config = promotion.typedConfig<BuyXGetXPromotionConfig>()
            resourcesProvider.getString(R.string.promotion_buy_x_get_x_description,
                product.name,
                config.minApplicableQuantity)
        }
    }
}
