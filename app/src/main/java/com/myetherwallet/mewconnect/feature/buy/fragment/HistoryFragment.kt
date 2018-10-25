package com.myetherwallet.mewconnect.feature.buy.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.observe
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.feature.buy.adapter.HistoryAdapter
import com.myetherwallet.mewconnect.feature.buy.data.PurchaseStatus
import com.myetherwallet.mewconnect.feature.buy.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by BArtWell on 18.09.2018.
 */
class HistoryFragment : BaseViewModelFragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private val adapter = HistoryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        history_loading.visibility = VISIBLE

        history_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        history_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })
        history_toolbar.setTitle(R.string.history_title)

        history_list.layoutManager = LinearLayoutManager(context)
        history_list.adapter = adapter

        viewModel = viewModel(viewModelFactory) {
            observe(data, ::onDataLoaded)
        }
        viewModel.load()
    }

    private fun onDataLoaded(statuses: List<PurchaseStatus>?) {
        statuses?.let {
            if (it.isEmpty()) {
                adapter.items = listOf()
                history_empty.visibility = VISIBLE
            } else {
                adapter.items = it.filter { purchaseStatus ->
                    purchaseStatus.status == PurchaseStatus.STATUS_IN_PROGRESS ||
                            purchaseStatus.status == PurchaseStatus.STATUS_APPROVED ||
                            purchaseStatus.status == PurchaseStatus.STATUS_DECLINED
                }
                history_empty.visibility = GONE
                adapter.notifyDataSetChanged()
            }
        }
        history_loading.visibility = GONE
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_history
}