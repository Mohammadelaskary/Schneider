package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.showErrorTextView
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingMenuFragment.Companion.IS_PO_VENDOR_KEY
import net.gbs.schneider.databinding.FragmentPOInvoiceListBinding

class PO_InvoiceListFragment :
    BaseFragmentWithViewModel<POInvoiceListViewModel, FragmentPOInvoiceListBinding>(),
    InvoiceAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = PO_InvoiceListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPOInvoiceListBinding
        get() = FragmentPOInvoiceListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            invoiceAdapter.filter.filter(text)
        })
    }

    private lateinit var invoiceAdapter: InvoiceAdapter
    private fun setUpInvoiceListRecyclerView() {
        invoiceAdapter = InvoiceAdapter(this)
        binding.invoiceList.adapter = invoiceAdapter
    }

    private fun observeInvoiceList() {
        viewModel.getInvoicesStatus.observe(requireActivity()) {
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
        viewModel.invoiceMutableLiveData.observe(requireActivity()) {
            if (it.isNotEmpty())
                invoiceAdapter.invoicesList = it
            else
                invoiceAdapter.invoicesList = listOf()
        }
    }

    override fun OnStartReceivingButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(INVOICE_KEY, Invoice.toJson(invoice))
        bundle.putBoolean(IS_PO_VENDOR_KEY,isPoVendor!!)
        NavigateTo(
            requireView(),
            R.id.action_PO_InvoiceListFragment_to_startReceivingFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(INVOICE_KEY, Invoice.toJson(invoice))
        NavigateTo(
            requireView(),
            R.id.action_PO_InvoiceListFragment_to_invoiceDetailsListFragment,
            bundle
        )
    }
    private var isPoVendor:Boolean? = null
    override fun onResume() {
        super.onResume()
        isPoVendor = arguments?.getBoolean(IS_PO_VENDOR_KEY)
        Log.d(TAG, "onViewCreated: $isPoVendor")
        viewModel.getInvoiceList(isPoVendor!!)
        changeTitle(getString(R.string.po_invoice_list), activity as MainActivity)
    }

}