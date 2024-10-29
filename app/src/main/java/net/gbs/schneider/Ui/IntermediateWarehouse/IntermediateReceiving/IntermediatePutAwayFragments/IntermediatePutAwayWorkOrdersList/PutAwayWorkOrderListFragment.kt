package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediatePutAwayWorkOrdersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.errorState
import net.gbs.schneider.Tools.Tools.loadingState
import net.gbs.schneider.Tools.Tools.successState
import net.gbs.schneider.databinding.FragmentPutAwayWorkOrderListBinding

class PutAwayWorkOrderListFragment :
    BaseFragmentWithViewModel<PutAwayWorkOrderListViewModel, FragmentPutAwayWorkOrderListBinding>(),
    IntermediatePutAwayWorkOrdersAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = PutAwayWorkOrderListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPutAwayWorkOrderListBinding
        get() = FragmentPutAwayWorkOrderListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeGettingWorkOrderList()
    }

    private lateinit var workOrdersAdapter: IntermediatePutAwayWorkOrdersAdapter
    private fun setUpRecyclerView() {
        workOrdersAdapter = IntermediatePutAwayWorkOrdersAdapter(this)
        binding.workOrdersList.adapter = workOrdersAdapter
        binding.search.editText?.doOnTextChanged { text, start, before, count ->
            workOrdersAdapter.filter.filter(text)
        }
    }

    private fun observeGettingWorkOrderList() {
        viewModel.getWorkOrderListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                    loadingState(binding.workOrdersList, binding.errorMessage)
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                }

                else -> {
                    loadingDialog.dismiss()
                    errorState(binding.workOrdersList, binding.errorMessage, it.message)
                }
            }
        }
        viewModel.getWorkOrderList.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                workOrdersAdapter.workOrdersList = it
                successState(binding.workOrdersList, binding.errorMessage)
            } else {
                errorState(
                    binding.workOrdersList,
                    binding.errorMessage,
                    getString(R.string.no_work_orders)
                )
            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getWorkOrderList()
        Tools.changeTitle(getString(R.string.work_order_list), activity as MainActivity)
        Tools.showToolBar(activity as MainActivity)
    }

    override fun OnFastPutAwayButtonClicked(workOrder: IntermediateWorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, IntermediateWorkOrder.toJson(workOrder))
        NavigateTo(
            requireView(),
            R.id.action_putAwayWorkOrderListFragment_to_intermediateFastPutAwayFragment,
            bundle
        )
    }

    override fun OnDetailedPutAwayClicked(workOrder: IntermediateWorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, IntermediateWorkOrder.toJson(workOrder))
        NavigateTo(
            requireView(),
            R.id.action_putAwayWorkOrderListFragment_to_intermediateDetailedPutAwayFragment,
            bundle
        )
    }
}