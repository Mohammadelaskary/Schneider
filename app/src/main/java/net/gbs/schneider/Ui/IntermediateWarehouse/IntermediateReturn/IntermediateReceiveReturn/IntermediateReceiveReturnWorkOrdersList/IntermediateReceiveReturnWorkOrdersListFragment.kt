package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReceiveReturn.IntermediateReceiveReturnWorkOrdersList

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
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediateReceiveReturnWorkOrdersListBinding

class IntermediateReceiveReturnWorkOrdersListFragment :
    BaseFragmentWithViewModel<IntermediateReceiveReturnWorkOrdersListViewModel, FragmentIntermediateReceiveReturnWorkOrdersListBinding>(),
    IntermediateReturnWorkOrdersListAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = IntermediateReceiveReturnWorkOrdersListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateReceiveReturnWorkOrdersListBinding
        get() = FragmentIntermediateReceiveReturnWorkOrdersListBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            workOrdersListAdapter.filter.filter(text)
        })
    }

    private lateinit var workOrdersListAdapter: IntermediateReturnWorkOrdersListAdapter
    private fun setUpInvoiceListRecyclerView() {
        workOrdersListAdapter = IntermediateReturnWorkOrdersListAdapter(this)
        binding.invoiceList.adapter = workOrdersListAdapter
    }

    private fun observeInvoiceList() {
        viewModel.gettingWorkOrdersListStatus.observe(requireActivity()) {
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
                    workOrdersListAdapter.invoicesList = listOf()
                }
            }
        }
        viewModel.gettingWorkOrdersListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty())
                workOrdersListAdapter.invoicesList = it
            else
                workOrdersListAdapter.invoicesList = listOf()
        }
    }

    override fun OnStartReturnButtonClicked(workOrderReturn: IntermediateWorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(
            IntentKeys.WORK_ORDER_KEY,
            IntermediateWorkOrderReturn.toJson(workOrderReturn)
        )
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediateReceiveReturnWorkOrdersListFragment2_to_intermediateReturnStartReceivingFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrderReturn: IntermediateWorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(
            IntentKeys.WORK_ORDER_KEY,
            IntermediateWorkOrderReturn.toJson(workOrderReturn)
        )
        Tools.NavigateTo(
            requireView(),
            R.id.action_intermediateReceiveReturnWorkOrdersListFragment2_to_intermediateReturnWorkOrderDetailsListFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWorkOrdersList()
        Tools.changeTitle(getString(R.string.work_order_list), activity as MainActivity)
    }
}