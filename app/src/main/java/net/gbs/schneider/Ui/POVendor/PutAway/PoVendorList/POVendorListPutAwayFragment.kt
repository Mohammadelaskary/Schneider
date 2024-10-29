package net.gbs.schneider.Ui.POVendor.PutAway.PoVendorList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList.InvoiceVendorPutAwayAdapter
import net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList.POVendorListPutAwayViewModel
import net.gbs.schneider.databinding.FragmentPOVendorListBinding

class POVendorListPutAwayFragment :
    BaseFragmentWithViewModel<POVendorListPutAwayViewModel, FragmentPOVendorListBinding>(),
    InvoiceVendorPutAwayAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = POVendorListPutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPOVendorListBinding
        get() = FragmentPOVendorListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInvoiceListRecyclerView()
        observeInvoiceList()
        binding.search.editText?.addTextChangedListener(onTextChanged = { text, start, before, count ->
            invoiceAdapter.filter.filter(text)
        })
    }

    private lateinit var invoiceAdapter: InvoiceVendorPutAwayAdapter
    private fun setUpInvoiceListRecyclerView() {
        invoiceAdapter = InvoiceVendorPutAwayAdapter(this)
        binding.invoiceList.adapter = invoiceAdapter
    }

    private fun observeInvoiceList() {
        viewModel.getPoVendorListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                    invoiceAdapter.invoicesList = listOf()
                }
            }
        }
        viewModel.getPoVendorListLiveData.observe(requireActivity()) {
            if (it.isNotEmpty())
                invoiceAdapter.invoicesList = it
            else
                invoiceAdapter.invoicesList = listOf()
        }
    }

    override fun OnStartPutAwayButtonClicked(invoice: InvoiceVendor) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.INVOICE_KEY, InvoiceVendor.toJson(invoice))
        Tools.NavigateTo(
            requireView(),
            R.id.action_POVendorListPutAwayFragment_to_startPutAwayPOVendorFragment,
            bundle
        )
    }

    override fun OnDetailsButtonClicked(invoice: InvoiceVendor) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.INVOICE_KEY, InvoiceVendor.toJson(invoice))
        Tools.NavigateTo(
            requireView(),
            R.id.action_POVendorListPutAwayFragment_to_vendorInvoiceDetailsListFragment,
            bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPoVendorInvoiceList()
        Tools.changeTitle(getString(R.string.po_vendor_invoice_list), activity as MainActivity)
    }

}