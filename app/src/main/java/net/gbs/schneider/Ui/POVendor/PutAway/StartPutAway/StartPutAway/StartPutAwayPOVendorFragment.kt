package net.gbs.schneider.Ui.POVendor.PutAway.StartPutAway

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.VendorMaterial
import net.gbs.schneider.R
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.PutAwayMaterialVendorListAdapter
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.PutAwayMaterialVendorListDialog
import net.gbs.schneider.Receiving.PutAway.SerializedMaterialListDialg.PutAwayVendorSerialsListDialog
import net.gbs.schneider.Receiving.PutAway.StartPutAway.StartPutAwayPOVendorViewModel
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler.OnEnterKeyPressed
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.containsOnlyDigits
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentStartVendorPutAwayBinding

class StartPutAwayPOVendorFragment :
    BaseFragmentWithViewModel<StartPutAwayPOVendorViewModel, FragmentStartVendorPutAwayBinding>(),
    PutAwayMaterialVendorListAdapter.OnMaterialItemClicked,
    com.honeywell.aidc.BarcodeReader.BarcodeListener,
    com.honeywell.aidc.BarcodeReader.TriggerListener,
    SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked,
    View.OnClickListener {

    companion object {
        fun newInstance() = StartPutAwayPOVendorFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartVendorPutAwayBinding
        get() = FragmentStartVendorPutAwayBinding::inflate
    private lateinit var materialListDialog: PutAwayMaterialVendorListDialog
    private lateinit var serialsListDialog: PutAwayVendorSerialsListDialog
    private lateinit var invoice: InvoiceVendor
    private lateinit var barcodeReader: BarcodeReader
    private lateinit var binList: List<Bin>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)

    }

    private fun fillData() {
        binding.let {
            materialListDialog =
                PutAwayMaterialVendorListDialog(requireContext(), invoice.materials, this)
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.projectNumber.text = invoice.projectNumber
            it.poNumber.text = invoice.poNumber
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoice = requireArguments().getString(IntentKeys.INVOICE_KEY)
            ?.let { InvoiceVendor.fromJson(it) }!!
        fillData()
        observeBinCodeData()
        observePutAwayResult()
        clearInputLayoutError(
            binding.binCode,
            binding.materialCode,
            binding.serialNo,
            binding.putAwayQty
        )
        binding.binCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.binCode.error = null

            }
        })
        binding.materialCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    binding.clearMaterialData.visibility = VISIBLE
                    binding.materialList.visibility = GONE
                } else {
                    binding.clearMaterialData.visibility = GONE
                    binding.materialList.visibility = VISIBLE
                }
            }
        })
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        setUpScannedSerialRecyclerView()
        binding.materialList.setOnClickListener {
            materialListDialog.show()
        }
        binding.clearMaterialData.setOnClickListener {
            binding.materialDataGroup.visibility = GONE
            selectedMaterial = null
            binding.materialCode.editText?.setText("")
        }
        binding.serialNo.setEndIconOnClickListener {
            serialsListDialog.show()
        }

        OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            val material = invoice.materials.find { it.materialCode == materialCode }
            if (material != null) {
                selectedMaterial = material
                fillMaterialData()
            } else {
                binding.materialCode.error = getString(R.string.wrong_material_code)
            }

        }
        OnEnterKeyPressed(binding.binCode) {
            val binCode = binding.binCode.editText?.text.toString().trim()
            viewModel.getBinData(binCode)
        }
        OnEnterKeyPressed(binding.serialNo) {
            val serialNo = binding.binCode.editText?.text.toString().trim()
            val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
            if (serial != null) {
                val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                if (scannedSerial == null) {
                    serial.isPutaway = true
                    scannedSerialsList.add(serial)
                    rejectedSerialsAdapter.notifyDataSetChanged()
                    binding.rejectedScannedQty.text =
                        "${scannedSerialsList.size} / ${selectedMaterial?.acceptedQty}"
                    serialsListDialog.setSerialAsScanned(serial)
                } else {
                    binding.serialNo.error =
                        getString(R.string.serial_added_before)
                }
            } else {
                binding.serialNo.error = getString(R.string.wrong_serial_no)
            }
        }
    }

    private fun observePutAwayResult() {
        viewModel.getResultInvoiceStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getResultInvoice.observe(requireActivity()) {
            if (it.materials.isEmpty())
                back(this)
            binding.materialDataGroup.visibility = GONE
            selectedMaterial = null
            binding.materialCode.editText?.setText("")
        }
    }

    private fun observeBinCodeData() {
        viewModel.getBinDataStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                    binding.locationLayout.visibility = GONE
                }

                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.binCode.error = it.message
                    binding.locationLayout.visibility = GONE
                }

                Status.NETWORK_FAIL -> {
                    loadingDialog.dismiss()
                    warningDialog(requireActivity(), it.message)
                    binding.locationLayout.visibility = GONE
                }

                else -> {
                    loadingDialog.dismiss()
                    binding.locationLayout.visibility = VISIBLE
                }
            }
        }
        viewModel.getBinData.observe(requireActivity()) {
            binding.binCode.editText?.setText(it.storageBinCode)
            val binData =
                "${it.warehouseName} -> ${it.storageLocationName} -> ${it.storageSectionCode} -> ${it.storageBinCode}"
            binding.locationLayout.visibility = VISIBLE
            binding.locationDetails.text = binData
        }
    }

    private lateinit var rejectedSerialsAdapter: SerialsWithRemoveButtonAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        rejectedSerialsAdapter = SerialsWithRemoveButtonAdapter(scannedSerialsList, this)
        binding.returnedSerials.adapter = rejectedSerialsAdapter
    }

    private var selectedMaterial: VendorMaterial? = null

    override fun onMaterialItemClicked(material: VendorMaterial) {
        selectedMaterial = material
        fillMaterialData()
        materialListDialog.dismiss()
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedCode = barcodeReader.scannedData(p0!!)
            if (binding.binCode.editText?.text!!.isEmpty()) {
                viewModel.getBinData(scannedCode)
            } else {
                if (binding.serialNo.visibility == GONE) {
                    val material = invoice.materials.find { it.materialCode == scannedCode }
                    if (material != null) {
                        selectedMaterial = material
                        fillMaterialData()
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }

                } else {
                    val serial = selectedMaterial!!.serials.find { it.serial == scannedCode }
                    if (serial != null) {
                        val scannedSerial = scannedSerialsList.find { it.serial == scannedCode }
                        if (scannedSerial == null) {
                            serial.isPutaway = true
                            scannedSerialsList.add(serial)
                            rejectedSerialsAdapter.notifyDataSetChanged()
                            binding.rejectedScannedQty.text =
                                "${scannedSerialsList.size} / ${selectedMaterial?.acceptedQty}"
                            serialsListDialog.setSerialAsScanned(serial)
                        } else {
                            binding.serialNo.error =
                                getString(R.string.serial_added_before)
                        }
                    } else {
                        binding.serialNo.error = getString(R.string.wrong_serial_no)
                    }
                }
            }
        }
    }

    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.qty.text = selectedMaterial!!.acceptedQty.toString()
            it.materialDataGroup.visibility = VISIBLE
            if (selectedMaterial!!.isSerialized) {
                binding.rejectedScannedQty.text =
                    "${scannedSerialsList.size} / ${selectedMaterial?.acceptedQty}"
                it.serialNo.visibility = VISIBLE
                it.rejectedScannedQty.visibility = VISIBLE
                it.returnedSerials.visibility = VISIBLE
                it.putAwayQty.visibility = GONE
                serialsListDialog =
                    PutAwayVendorSerialsListDialog(requireContext(), selectedMaterial!!)
            } else {
                it.serialNo.visibility = GONE
                it.rejectedScannedQty.visibility = GONE
                it.returnedSerials.visibility = GONE
                it.putAwayQty.visibility = VISIBLE
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
        Tools.changeTitle(getString(R.string.start_put_away), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }
//
//    override fun onSerialRejected(position: Int, isRejected: Boolean) {
//        scannedSerialsList[position].isRejected = isRejected
//        serialsListDialog.setSerialAsRejected(scannedSerialsList[position])
//    }
//
//    override fun onRemoveButtonClicked(position: Int) {
//        scannedSerialsList[position].isReceived = false
//        serialsListDialog.setSerialAsScanned(scannedSerialsList[position])
//        scannedSerialsList.removeAt(position)
//        rejectedSerialsAdapter.notifyDataSetChanged()
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val binCode = binding.binCode.editText?.text.toString().trim()
                if (binCode.isNotEmpty()) {
                    if (selectedMaterial != null) {
                        if (selectedMaterial!!.isSerialized) {
                            if (scannedSerialsList.isNotEmpty() && selectedMaterial!!.serials.isNotEmpty()) {
                                val putawayinvoiceSerializedbody = PutAwayVendor_SerializedBody(
                                    PoVendorHeaderId = invoice.poVendorHeaderId,
                                    PoVendorDetailId = selectedMaterial?.poVendorDetailId!!,
                                    serials = scannedSerialsList,
                                    StorageBinCode = binCode
                                )
                                viewModel.putAwayVendor_Serialized(putawayinvoiceSerializedbody)
                            } else {
                                binding.serialNo.error = getString(R.string.please_scan_serials)
                            }
                        } else {
                            val putAwayQtyText =
                                binding.putAwayQty.editText?.text.toString().trim()
                            if (putAwayQtyText.isNotEmpty()) {
                                if (containsOnlyDigits(putAwayQtyText)) {
                                    val putAwayQty = putAwayQtyText.toInt()
                                    if (putAwayQty <= selectedMaterial!!.acceptedQty) {
                                        val body = PutAwayVendor_UnserializedBody(
                                            PoVendorHeaderId = invoice.poVendorHeaderId,
                                            PoVendorDetailId = selectedMaterial!!.poVendorDetailId,
                                            StorageBinCode = binCode,
                                            PutAwayQty = putAwayQty
                                        )
                                        viewModel.putAwayInvoice_Unserialized(body)
                                    } else {
                                        binding.putAwayQty.error =
                                            getString(R.string.put_away_qty_must_be_less_or_equal_to) + " ${selectedMaterial!!.acceptedQty}"
                                    }
                                } else {
                                    binding.putAwayQty.error =
                                        getString(R.string.put_away_qty_must_contains_only_digits)
                                }
                            } else {
                                binding.putAwayQty.error =
                                    getString(R.string.please_enter_put_away_qty)
                            }
                        }
                    } else {
                        binding.materialCode.error =
                            getString(R.string.please_scan_or_select_material_first)
                    }
                } else {
                    binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
                }
            }
        }
    }

    override fun OnRemovedSerial(position: Int) {
        scannedSerialsList.removeAt(position)
        rejectedSerialsAdapter.notifyDataSetChanged()
    }
}