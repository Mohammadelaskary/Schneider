package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.ChangeSerialsPoPlantInvoicesList

import android.os.Bundle
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
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList.ChangeSerialsInvoiceAdapter
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList.ChangeSerialsPOInvoiceListViewModel
import net.gbs.schneider.databinding.FragmentPOInvoiceListBinding

class ChangeSerialsPO_InvoiceListFragment :
    BaseFragmentWithViewModel<ChangeSerialsPOInvoiceListViewModel, FragmentPOInvoiceListBinding>(),
    ChangeSerialsInvoiceAdapter.OnItemButtonsClicked {

    companion object {
        fun newInstance() = ChangeSerialsPO_InvoiceListFragment()
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

    private lateinit var invoiceAdapter: ChangeSerialsInvoiceAdapter
    private fun setUpInvoiceListRecyclerView() {
        invoiceAdapter = ChangeSerialsInvoiceAdapter(this)
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

    override fun onResume() {
        super.onResume()
        viewModel.getInvoiceList()
        changeTitle(getString(R.string.po_invoice_list), activity as MainActivity)
    }

    override fun OnStartSerialsButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(INVOICE_KEY, Invoice.toJson(invoice))
        NavigateTo(
            requireView(),
            R.id.action_changeSerialsPO_InvoiceListFragment_to_startChangingSerialsFragment,
            bundle
        )
    }

    override fun OnStartQtyButtonClicked(invoice: Invoice) {
        val bundle = Bundle()
        bundle.putString(INVOICE_KEY, Invoice.toJson(invoice))
        NavigateTo(
            requireView(),
            R.id.action_changeSerialsPO_InvoiceListFragment_to_startChangingQtyFragment,
            bundle
        )
    }
}