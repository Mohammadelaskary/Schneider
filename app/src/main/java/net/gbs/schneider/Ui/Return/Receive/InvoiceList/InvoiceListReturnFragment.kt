package net.gbs.schneider.Ui.Return.Receive.InvoiceList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.showErrorTextView
import net.gbs.schneider.databinding.FragmentInvoiceListReturnBinding

class InvoiceListReturnFragment :
    BaseFragmentWithViewModel<InvoiceListReturnViewModel, FragmentInvoiceListReturnBinding>(),
    ReturnInvoiceListAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = InvoiceListReturnFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInvoiceListReturnBinding
        get() = FragmentInvoiceListReturnBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            invoiceAdapter.filter.filter(text)
        })
    }

    private lateinit var invoiceAdapter: ReturnInvoiceListAdapter
    private fun setUpInvoiceListRecyclerView() {
        invoiceAdapter = ReturnInvoiceListAdapter(this)
        binding.invoiceList.adapter = invoiceAdapter
    }

    private fun observeInvoiceList() {
        viewModel.gettingInvoiceListStatus.observe(requireActivity()) {
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
                    invoiceAdapter.invoicesList = listOf()
                }
            }
        }
        viewModel.gettingInvoiceListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty())
                invoiceAdapter.invoicesList = it
            else
                invoiceAdapter.invoicesList = listOf()
        }
    }

    override fun OnStartReturnButtonClicked(invoice: WorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, WorkOrderReturn.toJson(invoice))
        Tools.NavigateTo(
            requireView(),
            R.id.action_invoiceListReturnFragment_to_startReturnFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(workOrderReturn: WorkOrderReturn) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.WORK_ORDER_KEY, WorkOrderReturn.toJson(workOrderReturn))
        Tools.NavigateTo(
            requireView(),
            R.id.action_invoiceListReturnFragment_to_returnWorkOrderDetailsListFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getInvoiceList()
        Tools.changeTitle(getString(R.string.work_order_list), activity as MainActivity)
    }

}