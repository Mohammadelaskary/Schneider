package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateIssueWorkOrdersList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.IntermediateIssueWorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediateIssueWorkOrdersListBinding

class IntermediateIssueWorkOrdersListFragment :
    BaseFragmentWithViewModel<IntermediateIssueWorkOrdersListViewModel, FragmentIntermediateIssueWorkOrdersListBinding>(),
    IntermediateIssueWorkOrdersAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = IntermediateIssueWorkOrdersListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateIssueWorkOrdersListBinding
        get() = FragmentIntermediateIssueWorkOrdersListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeGettingWorkOrderList()
    }

    private lateinit var workOrdersAdapter: IntermediateIssueWorkOrdersAdapter
    private fun setUpRecyclerView() {
        workOrdersAdapter = IntermediateIssueWorkOrdersAdapter(this)
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
                    Tools.loadingState(binding.workOrdersList, binding.errorMessage)
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.errorState(binding.workOrdersList, binding.errorMessage, it.message)
                }
            }
        }
        viewModel.getWorkOrderList.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                workOrdersAdapter.workOrdersList = it
                Tools.successState(binding.workOrdersList, binding.errorMessage)
            } else {
                Tools.errorState(
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
    }

    override fun OnStartIssueButtonClicked(workOrder: IntermediateIssueWorkOrder) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, IntermediateIssueWorkOrder.toJson(workOrder))
        Log.d(
            "IntermediateStartIssueFragment-OnStartIssueButtonClicked",
            "OnStartIssueButtonClicked: ${IntermediateIssueWorkOrder.toJson(workOrder)}"
        )
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediateIssueWorkOrdersListFragment_to_intermediateStartIssueFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrder: IntermediateIssueWorkOrder) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, IntermediateIssueWorkOrder.toJson(workOrder))
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediateIssueWorkOrdersListFragment_to_intermediateIssueWorkOrderDetailsListFragment,
            bundle
        )
    }
}