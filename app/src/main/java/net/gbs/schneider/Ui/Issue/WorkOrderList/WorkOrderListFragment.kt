package net.gbs.schneider.Ui.Issue.WorkOrderList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.showErrorTextView
import net.gbs.schneider.databinding.FragmentWorkOrderListBinding

class WorkOrderListFragment :
    BaseFragmentWithViewModel<WorkOrderListViewModel, FragmentWorkOrderListBinding>(),
    WorkOrderAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = WorkOrderListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWorkOrderListBinding
        get() = FragmentWorkOrderListBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWorkOrdersRecyclerView()

        observeWorkOrders()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, end, count ->
            workOrdersAdapter.filter.filter(text)
        })
    }

    private fun observeWorkOrders() {
        viewModel.workOrdersStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                    binding.errorMessage.visibility = GONE
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    binding.errorMessage.visibility = GONE
                }

                else -> {
                    loadingDialog.dismiss()
                    showErrorTextView(it.message, binding.errorMessage)
                    workOrdersAdapter.workOrdersList = listOf()
                }
            }
        }
        viewModel.workOrdersMutableLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                workOrdersAdapter.workOrdersList = it
            } else {
                workOrdersAdapter.workOrdersList = listOf()
            }
        }
    }

    private lateinit var workOrdersAdapter: WorkOrderAdapter
    private fun setUpWorkOrdersRecyclerView() {
        workOrdersAdapter = WorkOrderAdapter(this, requireContext())
        binding.workOrderList.adapter = workOrdersAdapter
    }

    override fun OnStartIssueingButtonClicked(workOrder: WorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, WorkOrder.toJson(workOrder))
        Tools.NavigateTo(
            requireView(),
            R.id.action_workOrderListFragment_to_startIssueingFragment, bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrder: WorkOrder) {
        val bundle = Bundle()
        bundle.putString(WORK_ORDER_KEY, WorkOrder.toJson(workOrder))
        Tools.NavigateTo(
            requireView(),
            R.id.action_workOrderListFragment_to_issueWorkOrderDetailsListFragment, bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWorkOrders()
        changeTitle(getString(R.string.work_order_list), activity as MainActivity)
    }
}