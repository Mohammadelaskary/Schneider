package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.InvoiceDetailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg.SerialsListDialog
import net.gbs.schneider.databinding.FragmentInvoiceDetailsListBinding

class InvoiceDetailsListFragment :
    BaseFragmentWithoutViewModel<FragmentInvoiceDetailsListBinding>(),
    InvoiceDetailsAdapter.OnShowSerialsButtonClicked {

    companion object {
        fun newInstance() = InvoiceDetailsListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInvoiceDetailsListBinding
        get() = FragmentInvoiceDetailsListBinding::inflate

    private lateinit var serialsListDialog: SerialsListDialog
    private lateinit var invoice: Invoice
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoice = requireArguments().getString(INVOICE_KEY)?.let { Invoice.fromJson(it) }!!
        setUpInvoiceDetailsListAdapter()
    }

    private lateinit var invoiceDetailsAdapter: InvoiceDetailsAdapter
    private fun setUpInvoiceDetailsListAdapter() {
        invoiceDetailsAdapter = InvoiceDetailsAdapter(invoice, this)
        binding.materialList.adapter = invoiceDetailsAdapter
    }

    override fun onShowSerialsButtonClicked(material: Material) {
        serialsListDialog = SerialsListDialog(requireContext(), material)
        serialsListDialog.show()
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.invoice_details), activity as MainActivity)
    }
}