package net.gbs.schneider.Ui.POVendor.PoVendorDetailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.databinding.FragmentVendorInvoiceDetailsListBinding

class VendorInvoiceDetailsListFragment :
    BaseFragmentWithoutViewModel<FragmentVendorInvoiceDetailsListBinding>() {

    companion object {
        fun newInstance() = VendorInvoiceDetailsListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVendorInvoiceDetailsListBinding
        get() = FragmentVendorInvoiceDetailsListBinding::inflate
    lateinit var invoiceVendor: InvoiceVendor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            invoiceVendor =
                requireArguments().getString(INVOICE_KEY)?.let { InvoiceVendor.fromJson(it) }!!

        }
        setUpDetailsListRecyclerView()
    }

    lateinit var adapter: InvoiceVendorDetailsAdapter
    private fun setUpDetailsListRecyclerView() {
        adapter = InvoiceVendorDetailsAdapter(invoice = invoiceVendor)
        binding.invoiceDetailsList.adapter = adapter
    }
}