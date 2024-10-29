package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.InvoiceListPutAway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentInvoiceListPutAwayBinding

class InvoiceListPutAwayFragment :
    BaseFragmentWithViewModel<InvoiceListPutAwayViewModel, FragmentInvoiceListPutAwayBinding>(),
    InvoicePutAwayAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = InvoiceListPutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInvoiceListPutAwayBinding
        get() = FragmentInvoiceListPutAwayBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            invoiceAdapter.filter.filter(text)
        })
    }

    private lateinit var invoiceAdapter: InvoicePutAwayAdapter
    private fun setUpInvoiceListRecyclerView() {
        invoiceAdapter = InvoicePutAwayAdapter(this)
        binding.invoiceList.adapter = invoiceAdapter
    }

    private fun observeInvoiceList() {
        viewModel.getInvoiceListStatus.observe(requireActivity()) {
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
                    invoiceAdapter.invoicesList = listOf()
                }
            }
        }
        viewModel.getInvoiceList.observe(requireActivity()) {
            if (it.isNotEmpty())
                invoiceAdapter.invoicesList = it
            else
                invoiceAdapter.invoicesList = listOf()
        }
    }

    override fun OnDetailedPutAwayButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.INVOICE_KEY, Invoice.toJson(invoice))
        Tools.NavigateTo(
            requireView(),
            R.id.action_invoiceListPutAwayFragment_to_startPutAwayFragment,
            bundle
        )
    }

    override fun OnFastPutAwayButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.INVOICE_KEY, Invoice.toJson(invoice))
        Tools.NavigateTo(
            requireView(),
            R.id.action_invoiceListPutAwayFragment_to_fastPutAwayFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getInvoiceList()
        Tools.changeTitle(getString(R.string.po_invoice_list), activity as MainActivity)
    }
}