package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingWorkOrdersList

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
import net.gbs.schneider.databinding.FragmentReceivingWorkOrderListBinding

class ReceivingWorkOrderListFragment :
    BaseFragmentWithViewModel<ReceivingWorkOrderListViewModel, FragmentReceivingWorkOrderListBinding>(),
    IntermediateReceivingWorkOrdersAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = ReceivingWorkOrderListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReceivingWorkOrderListBinding
        get() = FragmentReceivingWorkOrderListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeGettingWorkOrderList()
    }

    private lateinit var workOrdersAdapter: IntermediateReceivingWorkOrdersAdapter
    private fun setUpRecyclerView() {
        workOrdersAdapter = IntermediateReceivingWorkOrdersAdapter(this)
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
        Tools.showToolBar(activity as MainActivity)
        Tools.changeTitle(getString(R.string.work_order_list), activity as MainActivity)
    }

    override fun OnStartReceivingButtonClicked(workOrder: IntermediateWorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, IntermediateWorkOrder.toJson(workOrder))
        NavigateTo(
            requireView(),
            R.id.action_receivingWorkOrderListFragment_to_intermediateStartReceivingFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrder: IntermediateWorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, IntermediateWorkOrder.toJson(workOrder))
        NavigateTo(
            requireView(),
            R.id.action_receivingWorkOrderListFragment_to_intermediateWorkOrderDetailsFragment,
            bundle
        )
    }
}