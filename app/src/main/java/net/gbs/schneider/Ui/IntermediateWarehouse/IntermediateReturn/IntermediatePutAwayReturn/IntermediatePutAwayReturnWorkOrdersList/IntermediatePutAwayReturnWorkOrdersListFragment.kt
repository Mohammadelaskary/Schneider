package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediatePutAwayReturnWorkOrdersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Return.Receive.InvoiceList.IntermediatePutAwayReturnWorkOrdersListAdapter.IntermediatePutAwayReturnWorkOrdersListAdapter
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediatePutAwayReturnWorkOrdersListBinding

class IntermediatePutAwayReturnWorkOrdersListFragment :
    BaseFragmentWithViewModel<IntermediatePutAwayReturnWorkOrdersListViewModel, FragmentIntermediatePutAwayReturnWorkOrdersListBinding>(),
    IntermediatePutAwayReturnWorkOrdersListAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = IntermediatePutAwayReturnWorkOrdersListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediatePutAwayReturnWorkOrdersListBinding
        get() = FragmentIntermediatePutAwayReturnWorkOrdersListBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            workOrdersAdapter.filter.filter(text)
        })
    }

    private lateinit var workOrdersAdapter: IntermediatePutAwayReturnWorkOrdersListAdapter
    private fun setUpInvoiceListRecyclerView() {
        workOrdersAdapter = IntermediatePutAwayReturnWorkOrdersListAdapter(this)
        binding.invoiceList.adapter = workOrdersAdapter
    }

    private fun observeInvoiceList() {
        viewModel.getPutAwayWorkOrderStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                    binding.errorMessage.visibility = View.GONE
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    binding.errorMessage.visibility = View.GONE
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.showErrorTextView(it.message, binding.errorMessage)
                    workOrdersAdapter.workOrdersList = listOf()
                }
            }
        }
        viewModel.getPutAwayWorkOrderLiveData.observe(requireActivity()) {
            if (it.isNotEmpty())
                workOrdersAdapter.workOrdersList = it
            else
                workOrdersAdapter.workOrdersList = listOf()
        }
    }

    override fun OnStartReturnButtonClicked(workOrder: IntermediateWorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, IntermediateWorkOrderReturn.toJson(workOrder))
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediatePutAwayReturnWorkOrdersListFragment_to_intermediateReturnStartPutAwayFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrder: IntermediateWorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, IntermediateWorkOrderReturn.toJson(workOrder))
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediatePutAwayReturnWorkOrdersListFragment_to_intermediateReturnWorkOrderDetailsListFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWorkOrderList()
        Tools.changeTitle(getString(R.string.work_order_list), activity as MainActivity)
    }

}