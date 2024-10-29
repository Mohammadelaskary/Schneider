package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.FastPutAway

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayInvoiceBody
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.R
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentFastPutAwayBinding

class FastPutAwayFragment :
    BaseFragmentWithViewModel<FastPutAwayViewModel, FragmentFastPutAwayBinding>(), OnClickListener,
    FastPutAwayItemsAdapter.OnFastPutAwayItemClicked, BarcodeListener, TriggerListener {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFastPutAwayBinding
        get() = FragmentFastPutAwayBinding::inflate

    private lateinit var invoice: Invoice
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearInputLayoutError(binding.binCode)
        if (arguments != null) {
            invoice = Invoice.fromJson(arguments?.getString(INVOICE_KEY)!!)
            fillData()
            setUpRecyclerView()
        }
        binding.save.setOnClickListener(this)
        observeGettingBinCodeData()
        observeFastPutAway()
        binding.selectAll.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                invoice.materials.forEach {
                    it.isSelected = true
                    if (!selectedItemsIds.contains(it.poPlantDetailId))
                        selectedItemsIds.add(it.poPlantDetailId)
                    compoundButton.isEnabled = false
                }
                selectedItemsIds.forEach {
                    Log.d(TAG, "onItemClicked: ${it}")
                }
                materialAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeFastPutAway() {
        viewModel.fastPutAwayStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    showSuccessAlerter(it.message, requireActivity())
                    back(this)
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun observeGettingBinCodeData() {
        viewModel.getBinCodeStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.binCode.error = it.message
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getBinCodeLiveData.observe(requireActivity()) {
            try {
                binding.binCode.editText?.setText(it[0].storageBinCode)
            } catch (ex: Exception) {
                warningDialog(requireContext(), ex.message.toString())
            }

        }
    }

    private lateinit var materialAdapter: FastPutAwayItemsAdapter
    private fun setUpRecyclerView() {
        materialAdapter = FastPutAwayItemsAdapter(invoice.materials, requireContext(), this)
        binding.recyclerView.adapter = materialAdapter
    }

    private fun fillData() {
        with(binding) {
            plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            invoiceNumber.text = invoice.invoiceNumber
            poNumber.text = invoice.poNumber
            salesOrderNumber.text = invoice.salesOrderNumber
            projectNumber.text = invoice.projectNumber
        }
    }

    private val selectedItemsIds: MutableList<Int> = mutableListOf()

    override fun onItemClicked(position: Int, isSelected: Boolean) {
        invoice.materials[position].isSelected = !isSelected
        materialAdapter.notifyDataSetChanged()

        if (isSelected) {
            binding.selectAll.isChecked = false
            selectedItemsIds.remove(element = invoice.materials[position].poPlantDetailId)
            binding.selectAll.isEnabled = true
        } else {
            if (!selectedItemsIds.contains(invoice.materials[position].poPlantDetailId))
                selectedItemsIds.add(invoice.materials[position].poPlantDetailId)
            binding.selectAll.isChecked = selectedItemsIds.size == invoice.materials.size
        }

        selectedItemsIds.forEach {
            Log.d(TAG, "onItemClicked: ${it}")
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            try {
                val scannedText = barcodeReader.scannedData(p0!!)
                if (scannedText.isNotEmpty()) {
                    binding.binCode.editText?.setText("")
                    viewModel.getBinCodeData(scannedText)
                } else {
                    binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
                }
            } catch (ex: Exception) {
                warningDialog(requireContext(), ex.message.toString())
            }
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.fast_put_away), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val binCode = binding.binCode.editText?.text.toString().trim()
                if (readyToSave(binCode)) {
                    val body = FastPutAwayInvoiceBody(
                        IsFullPutaway = binding.selectAll.isChecked,
                        StorageBinCode = binCode,
                        PoPlantHeaderId = invoice.poPlantHeaderId,
                        PoPlantDetailIds = selectedItemsIds
                    )
                    viewModel.fastPutAway(body)
                }
            }
        }
    }

    private fun readyToSave(binCode: String): Boolean {
        var isReady = true
        if (binCode.isEmpty()) {
            isReady = false
            binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
        }
        if (selectedItemsIds.isEmpty()) {
            isReady = false
            warningDialog(requireContext(), getString(R.string.please_select_items_to_put_away))
        }
        return isReady
    }
}