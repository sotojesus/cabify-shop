package com.jesussoto.android.cabifyshop.ui.cart.promotions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jesussoto.android.cabifyshop.R
import com.jesussoto.android.cabifyshop.data.model.ProcessedCartItem
import com.jesussoto.android.cabifyshop.databinding.SheetPromotionsAppliedBinding
import com.jesussoto.android.cabifyshop.di.ViewModelFactory
import com.jesussoto.android.cabifyshop.ui.util.ResourcesProvider
import dagger.android.support.AndroidSupportInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

internal class PromotionsAppliedSheet: BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var resourcesProvider: ResourcesProvider

    private val viewModel: PromotionsAppliedViewModel by viewModels { viewModelFactory }

    private lateinit var binding: SheetPromotionsAppliedBinding

    private val adapter: PromotionsAppliedAdapter by lazy { PromotionsAppliedAdapter(resourcesProvider) }

    private var disposables: CompositeDisposable? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SheetPromotionsAppliedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { dismiss() }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val dividerDecorator = DividerItemDecoration(this.context, layoutManager.orientation)

        with(binding.promotionsRecyclerView) {
            this.adapter = this@PromotionsAppliedSheet.adapter
            this.layoutManager = layoutManager
            this.addItemDecoration(dividerDecorator)
        }
    }

    override fun onStart() {
        super.onStart()
        bindViewModel()
    }

    override fun onStop() {
        super.onStop()
        unbindViewModel()
    }

    private fun bindViewModel() {
        disposables = CompositeDisposable()
        disposables?.add(viewModel.getItemsWithPromotion()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::updateItems))
    }

    private fun unbindViewModel() {
        disposables?.dispose()
        disposables = null
    }

    private fun updateItems(itemsWithPromotion: List<ProcessedCartItem>) {
        binding.toolbar.title = getString(R.string.promotion_applied_title, itemsWithPromotion.size)
        adapter.submitList(itemsWithPromotion)
    }

    companion object {
        val TAG = PromotionsAppliedSheet::class.simpleName
    }
}
