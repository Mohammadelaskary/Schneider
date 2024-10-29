package net.gbs.schneider.Ui.POVendor.Receiving.PoVendorStartReceiving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.Model.RejectionReason
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.VendorMaterial
import net.gbs.schneider.R
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialVendorListAdapter
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialVendorListDialog
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentPOVendorStartReceivingBinding

class POVendorStartReceivingFragment :
    BaseFragmentWithViewModel<POVendorStartReceivingViewModel, FragmentPOVendorStartReceivingBinding>(),
    OnClickListener, BarcodeListener, TriggerListener,
    MaterialVendorListAdapter.OnMaterialItemClicked,
    ScannedVendorSerialsAdapter.OnSerialActionClicked {

    companion object {
        fun newInstance() = POVendorStartReceivingFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPOVendorStartReceivingBinding
        get() = FragmentPOVendorStartReceivingBinding::inflate

    private lateinit var materialListDialog: MaterialVendorListDialog
    private lateinit var invoice: InvoiceVendor
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        materialListDialog = MaterialVendorListDialog(requireContext(), invoice.materials, this)
        binding.let {
            it.projectNumber.text = invoice.projectNumber
            it.poNumber.text = invoice.poNumber
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoice = requireArguments().getString(IntentKeys.INVOICE_KEY)
            ?.let { InvoiceVendor.fromJson(it) }!!
        fillData()
        viewModel.getRejectionRequestsList()
        setUpRejectionRequestsSpinner()
        observeGettingRejectionRequestsList()
        Tools.clearInputLayoutError(
            binding.serialNo,
            binding.materialCode,
            binding.countedQty,
            binding.rejectedQty,
            binding.invoiceNumber
        )
        binding.inspect.visibility = GONE
        Tools.attachButtonsToListener(
            this,
            binding.save,
            binding.receivedList,
            binding.clearMaterialData,
            binding.materialList
        )
        Tools.clearInputLayoutError(
            binding.materialCode,
            binding.serialNo,
            binding.countedQty,
            binding.rejectedQty
        )
        setUpScannedSerialRecyclerView()


        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            val material = invoice.materials.find { materialCode == it.materialCode }
            if (material != null) {
                selectedMaterial = material
                fillMaterialData()
            } else {
                binding.materialCode.error = getString(R.string.wrong_material_code)
            }
        }
        observeReceiving()
        watchMaterialCodeText()
    }

    private var rejectionReasonsList: List<RejectionReason> = listOf()
    private lateinit var rejectionReasonsAdapter: ArrayAdapter<RejectionReason>
    private var selectedRejectionReason: RejectionReason? = null
    private fun setUpRejectionRequestsSpinner() {
        rejectionReasonsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            rejectionReasonsList
        )
        binding.rejectionReasonSpinner.setAdapter(rejectionReasonsAdapter)
        binding.rejectionReasonSpinner.setOnItemClickListener { adapterView, view, position, l ->
            selectedRejectionReason = rejectionReasonsList[position]
        }
    }

    private fun observeGettingRejectionRequestsList() {
        viewModel.getRejectionRequestsStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getRejectionRequestsLiveData.observe(requireActivity()) {
            rejectionReasonsList = it
            rejectionReasonsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                rejectionReasonsList
            )
            binding.rejectionReasonSpinner.setAdapter(rejectionReasonsAdapter)
        }
    }

    private fun watchMaterialCodeText() {
        binding.materialCode.editText?.doOnTextChanged { text, start, before, count ->
            if (text?.isEmpty()!!) {
                binding.clearMaterialData.visibility = GONE
                binding.materialList.visibility = View.VISIBLE
            } else {
                binding.clearMaterialData.visibility = View.VISIBLE
                binding.materialList.visibility = GONE
            }
        }
    }

    private fun observeReceiving() {
        viewModel.resultInvoiceLiveData.observe(requireActivity()) {
            invoice = it
            if (invoice.materials.isEmpty())
                back(this)
            else
                fillData()
        }
        viewModel.resultInvoiceStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                    binding.materialDataGroup.visibility = GONE
                    clearMaterialData()
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.serialNo.editText?.setText("")
        scannedSerialsList.clear()
        scannedSerialsAdapter.notifyDataSetChanged()
    }

    private lateinit var scannedSerialsAdapter: ScannedVendorSerialsAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        scannedSerialsAdapter = ScannedVendorSerialsAdapter(this, scannedSerialsList)
        binding.scannedSerials.adapter = scannedSerialsAdapter
    }

    private var selectedMaterial: VendorMaterial? = null

    override fun onMaterialItemClicked(material: VendorMaterial) {
        selectedMaterial = material
        fillMaterialData()
        materialListDialog.dismiss()
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (binding.serialNo.visibility == GONE) {
                for (material in invoice.materials) {
                    if (scannedText == material.materialCode) {
                        selectedMaterial = material
                        fillMaterialData()
                        break
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }
                }
            } else {
                val serial = scannedSerialsList.find { it.serial == scannedText }
                if (serial == null) {
                    if (scannedSerialsList.size < selectedMaterial!!.shippedQuantity) {
                        binding.serialNo.editText?.setText(scannedText)
                        scannedSerialsList.add(Serial(serial = scannedText, isReceived = true))
                        scannedSerialsAdapter.notifyDataSetChanged()
                        binding.scannedQty.text =
                            "${scannedSerialsList.size} / ${selectedMaterial!!.shippedQuantity}"
                    } else {
                        binding.serialNo.error = getString(R.string.all_serials_are_scanned)
                    }
                } else {
                    binding.serialNo.error = getString(R.string.serial_added_before)
                }
            }
        }
    }

    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.qty.text = selectedMaterial!!.shippedQuantity.toString()
            it.materialDataGroup.visibility = View.VISIBLE
            it.scannedQty.text =
                "${scannedSerialsList.size} / ${selectedMaterial!!.shippedQuantity}"
            if (selectedMaterial!!.isSerialized) {
                it.serialNo.visibility = View.VISIBLE
                it.scannedQty.visibility = View.VISIBLE
                it.scannedSerials.visibility = View.VISIBLE
                it.countedQty.visibility = GONE
                it.rejectedQty.visibility = GONE
            } else {
                it.serialNo.visibility = GONE
                it.scannedQty.visibility = GONE
                it.scannedSerials.visibility = GONE
                it.countedQty.visibility = View.VISIBLE
                it.rejectedQty.visibility = View.VISIBLE
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
        Tools.changeTitle(getString(R.string.start_receiving), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onSerialRejected(position: Int, isRejected: Boolean) {
        scannedSerialsList[position].isRejected = isRejected
    }

    override fun onSerialRemoved(position: Int) {
        scannedSerialsList.removeAt(position)
        scannedSerialsAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val invoiceNumber = binding.invoiceNumber.editText?.text.toString().trim()
                if (invoiceNumber.isNotEmpty()) {
                    if (selectedMaterial != null) {
                        if (selectedMaterial!!.isSerialized) {
                            if (scannedSerialsList.isNotEmpty()) {
                                viewModel.ReceiveVendor_Serialized(
                                    serials = scannedSerialsList,
                                    poVendorDetailId = selectedMaterial!!.poVendorDetailId,
                                    poVendorHeaderId = invoice.poVendorHeaderId!!,
                                    invoiceNumber = invoiceNumber,

                                    )
                            } else {
                                binding.serialNo.error = getString(R.string.please_scan_serials)
                            }
                        } else {
                            val receivedQtyText =
                                binding.countedQty.editText?.text.toString().trim()
                            val rejectedQtyText =
                                binding.rejectedQty.editText?.text.toString().trim()
                            if (receivedQtyText.isNotEmpty()) {
                                if (Tools.containsOnlyDigits(receivedQtyText)) {
                                    if (rejectedQtyText.isNotEmpty()) {
                                        if (Tools.containsOnlyDigits(rejectedQtyText)) {
                                            val receivedQty = receivedQtyText.toInt()
                                            val rejectedQty = rejectedQtyText.toInt()
                                            viewModel.ReceiveVendor_Unserialized(
                                                poVendorDetailId = selectedMaterial!!.poVendorDetailId,
                                                invoiceNumber = invoiceNumber,
                                                poVendorHeaderId = invoice.poVendorHeaderId!!,
                                                receivedQty = receivedQty,
                                                rejectedQty = rejectedQty,
                                            )
                                        } else {
                                            binding.countedQty.error =
                                                getString(R.string.please_enter_valid_received_quantity)
                                        }
                                    } else {
                                        binding.countedQty.error =
                                            getString(R.string.please_enter_received_quantity)
                                    }
                                } else {
                                    binding.countedQty.error =
                                        getString(R.string.please_enter_valid_received_quantity)
                                }
                            } else {
                                binding.countedQty.error =
                                    getString(R.string.please_enter_received_quantity)
                            }
                        }
                    } else {
                        binding.materialCode.error =
                            getString(R.string.please_scan_or_select_material_first)
                    }
                } else {
                    binding.invoiceNumber.error = getString(R.string.please_enter_invoice_number)
                }
            }

            R.id.received_list -> {
                materialListDialog.show()
            }

            R.id.clear_material_data -> {
                clearMaterialData()
            }

            R.id.material_list -> {
                materialListDialog.show()
            }
        }
    }
}