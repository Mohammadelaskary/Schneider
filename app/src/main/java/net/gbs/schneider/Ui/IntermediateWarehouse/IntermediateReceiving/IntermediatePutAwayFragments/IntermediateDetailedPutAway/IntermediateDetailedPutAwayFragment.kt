package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.showToolBar
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway.IntermediatePutAwayMaterialListDialog.IntermediatePutAwayMaterialListAdapter
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway.IntermediatePutAwaySerialsListDialog.IntermediatePutAwaySerialsListDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.IntermediatePutAwayMaterialListDialog
import net.gbs.schneider.databinding.FragmentIntermediateDetailedPutAwayBinding

class IntermediateDetailedPutAwayFragment :
    BaseFragmentWithViewModel<IntermediateDetailedPutAwayViewModel, FragmentIntermediateDetailedPutAwayBinding>(),
    BarcodeListener, TriggerListener, OnClickListener,
    SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = IntermediateDetailedPutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateDetailedPutAwayBinding
        get() = FragmentIntermediateDetailedPutAwayBinding::inflate

    private lateinit var materialListDialog: IntermediatePutAwayMaterialListDialog
    private lateinit var serialsListDialog: IntermediatePutAwaySerialsListDialog
    private lateinit var workOrder: IntermediateWorkOrder
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)

    }

    private fun fillData() {
        with(binding) {
            materialListDialog = IntermediatePutAwayMaterialListDialog(
                requireContext(),
                workOrder.materials,
                object : IntermediatePutAwayMaterialListAdapter.OnMaterialItemClicked {
                    override fun onMaterialItemClicked(material: IntermediateMaterial) {
                        selectedMaterial = material
                        fillMaterialData()
                        materialListDialog.dismiss()
                    }

                })
            warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            header.workOrderNumber.text = workOrder.workOrderNumber
            header.projectTo.text = workOrder.projectNumber
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workOrder = requireArguments().getString(IntentKeys.WORK_ORDER_KEY)
            ?.let { IntermediateWorkOrder.fromJson(it) }!!
        fillData()
        observeBinCodeData()
        observePutAwayResult()
        Tools.clearInputLayoutError(
            binding.binCode,
            binding.materialCode,
            binding.serializedScanLayout.serialNo,
            binding.oneSerialItemScanLayout.serialNo,
            binding.oneSerialItemScanLayout.countedQty,
            binding.unserializedEnterQtyLayout.countedQty,
            binding.binCode
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
                    binding.materialList.visibility = View.INVISIBLE
                } else {
                    binding.clearMaterialData.visibility = View.INVISIBLE
                    binding.materialList.visibility = VISIBLE
                }
            }
        })
        binding.oneSerialItemScanLayout.serialNo.editText?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    binding.oneSerialItemScanLayout.clearSerial.visibility = VISIBLE
                } else {
                    binding.oneSerialItemScanLayout.clearSerial.visibility = GONE
                }
            }
        })

        binding.oneSerialItemScanLayout.clearSerial.setOnClickListener {
            binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
            binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
            scannedQty = 0
            binding.oneSerialItemScanLayout.serialNo.isEnabled = true
        }
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        setUpScannedSerialRecyclerView()
        binding.materialList.setOnClickListener {
            materialListDialog.show()
        }
        binding.clearMaterialData.setOnClickListener {
            clearMaterialData()
        }
        binding.serializedScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }

        binding.oneSerialItemScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }

        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            val material = workOrder.materials.find { it.materialCode == materialCode }
            if (material != null) {
                selectedMaterial = material
                fillMaterialData()
            } else {
                binding.materialCode.error = getString(R.string.wrong_material_code)
            }

        }
        EditTextActionHandler.OnEnterKeyPressed(binding.binCode) {
            val binCode = binding.binCode.editText?.text.toString().trim()
            viewModel.getBinData(binCode)
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.serializedScanLayout.serialNo) {
            val serialNo = binding.serializedScanLayout.serialNo.editText?.text.toString().trim()
            val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
            if (serial != null) {
                val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                if (scannedSerial == null) {
                    serial.isPutaway = true
                    binding.serializedScanLayout.serialNo.editText?.setText("")
                    scannedSerialsList.add(serial)
                    scannedSerialsAdapter.notifyDataSetChanged()
                    binding.serializedScanLayout.scannedQty.text =
                        "${scannedSerialsList.size}"
                    serialsListDialog.setSerialAsScanned(serial)
                } else {
                    warningDialog(
                        requireContext(),
                        getString(R.string.serial_added_before))
                }
            } else {
                warningDialog(
                    requireContext(), getString(R.string.wrong_serial_no))
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = binding.oneSerialItemScanLayout.serialNo.editText?.text.toString().trim()
            val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
            if (serial != null) {
                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                    1
                } else {
                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                }
                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                if (scannedQty > selectedMaterial!!.remainingQtyPutAway){
                    binding.oneSerialItemScanLayout.countedQty.error =
                        getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingQtyPutAway
                } else if (scannedQty == selectedMaterial!!.remainingQtyPutAway) {
                    if (isMax){
                        warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                    }
                    isMax = true
                }
            } else {
                binding.oneSerialItemScanLayout.serialNo.error = getString(R.string.wrong_serial_no)
            }
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.serializedScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        scannedSerialsList.forEach { scannedSerial ->
            selectedMaterial?.serials?.forEach{ serial ->
                if (serial.serial==scannedSerial.serial){
                    serial.isPutaway = false
                }
            }
        }
        scannedSerialsList.clear()
        scannedSerialsAdapter.notifyDataSetChanged()
        selectedMaterial = null
        binding.materialCode.editText?.setText("")
        scannedQty = 0
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        scannedSerialsList.clear()
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        scannedSerialsAdapter.notifyDataSetChanged()
        binding.serializedScanLayout.serialNo.editText?.setText("")
    }

    private fun observePutAwayResult() {
        viewModel.getResultWorkOrderStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getResultWorkOrder.observe(requireActivity()) {
            if (it.materials.isEmpty())
                Tools.back(this)
            workOrder = it
            fillData()
            clearMaterialData()
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
                    Tools.warningDialog(requireActivity(), it.message)
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

    private lateinit var scannedSerialsAdapter: SerialsWithRemoveButtonAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        scannedSerialsAdapter = SerialsWithRemoveButtonAdapter(
            scannedSerialsList,
            this as SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked
        )
        binding.serializedScanLayout.scannedSerials.adapter = scannedSerialsAdapter
    }

    private var selectedMaterial: IntermediateMaterial? = null
    private var scannedQty = 0
    private var isMax = false
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedCode = barcodeReader.scannedData(p0!!)
            if (binding.binCode.editText?.text!!.isEmpty()) {
                viewModel.getBinData(scannedCode)
            } else {
                if (selectedMaterial == null) {
                    val material = workOrder.materials.find { it.materialCode == scannedCode }
                    if (material != null) {
                        selectedMaterial = material
                        fillMaterialData()
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }

                } else {
                    if (selectedMaterial!!.isSerializedWithOneSerial) {
                        val serial = selectedMaterial!!.serials.find { it.serial == scannedCode }
                        if (serial != null) {
                            if (getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!).isEmpty()){
                                binding.oneSerialItemScanLayout.serialNo.editText?.setText(scannedCode)
                                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                                    1
                                } else {
                                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                                }
                                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                                if (scannedQty > selectedMaterial!!.remainingQtyPutAway){
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingQtyPutAway
                                } else if (scannedQty == selectedMaterial!!.remainingQtyPutAway) {
                                    if (isMax){
                                        warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                                    }
                                    isMax = true
                                }

                            } else {
                                scannedQty =
                                    if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()) {
                                        1
                                    } else {
                                        getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                                    }
                                if (scannedQty > selectedMaterial!!.remainingQtyPutAway) {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQtyPutAway
                                } else if (scannedQty < selectedMaterial!!.remainingQtyPutAway) {
                                    scannedQty++
                                    binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                                } else {
                                    if (isMax) {
                                        warningDialog(
                                            requireContext(),
                                            getString(R.string.all_qty_are_scanned)
                                        )
                                    }
                                    isMax = true
                                }
                            }
                        } else binding.oneSerialItemScanLayout.serialNo.error =
                            getString(R.string.wrong_serial_no)
                    } else if (selectedMaterial!!.isSerialized) {
                        val serial = selectedMaterial!!.serials.find { it.serial == scannedCode }
                        if (serial != null) {
                            val scannedSerial = scannedSerialsList.find { it.serial == scannedCode }
                            if (scannedSerial == null) {
                                if (!serial.isPutaway) {
                                    binding.serializedScanLayout.serialNo.error = null
                                    serial.isPutaway = true
                                    scannedSerialsList.add(serial)
                                    scannedSerialsAdapter.notifyDataSetChanged()
                                    binding.serializedScanLayout.scannedQty.text =
                                        "${scannedSerialsList.size}"
                                    serialsListDialog.setSerialAsScanned(serial)
                                } else {
                                    warningDialog(
                                            requireContext(),
                                        getString(R.string.serial_already_put_away))
                                }
                            } else {
                                warningDialog(
                                            requireContext(),
                                    getString(R.string.serial_added_before))
                            }
                        } else {
                            warningDialog(
                                            requireContext(),
                                getString(R.string.wrong_serial_no))
                        }
                    }
                }
            }
        }
    }

    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.putAwayQtyPerAcceptedQty.text = "${selectedMaterial!!.remainingQtyPutAway}"
            it.materialDataGroup.visibility = VISIBLE
            if (selectedMaterial!!.isSerializedWithOneSerial) {
                showOneSerialLayout()
                serialsListDialog =
                    IntermediatePutAwaySerialsListDialog(requireContext(), selectedMaterial!!)
                binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
            } else {
                if (selectedMaterial!!.isSerialized) {
                    showSerializedLayout()
                    binding.serializedScanLayout.scannedQty.text =
                        "0"
                    serialsListDialog =
                        IntermediatePutAwaySerialsListDialog(requireContext(), selectedMaterial!!)
                } else {
                    showUnSerializedLayout()
                    binding.unserializedEnterQtyLayout.countedQty.editText?.setText("")
                }
            }
        }
    }

    private fun showSerializedLayout() {
        binding.serializedScanLayout.root.visibility = VISIBLE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
    }

    private fun showOneSerialLayout() {
        binding.serializedScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = VISIBLE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
    }

    private fun showUnSerializedLayout() {
        binding.serializedScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = VISIBLE
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.start_put_away), activity as MainActivity)
        showToolBar(activity as MainActivity)
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
                if (binCode.isNotEmpty()) {
                    if (selectedMaterial != null) {
                        if (selectedMaterial!!.isSerializedWithOneSerial) {
                            val serialCode =
                                getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!)
                            val qtyText =
                                getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!)
                            if (serialCode.isNotEmpty()) {
                                if (qtyText.isNotEmpty()) {
                                    val qty: Int
                                    try {
                                        qty = qtyText.toInt()
                                        val body = PutAwayProductionWorkOrder_SerializedBody(
                                            workOrderIssueId = workOrder.workOrderIssueId,
                                            workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                            bulkQty = qty,
                                            serials = listOf(
                                                Serial(
                                                    serial = serialCode,
                                                    isPutaway = true,
                                                )
                                            ),
                                            isBulk = true,
                                            storageBinCode = binCode
                                        )
                                        viewModel.putAwayInvoice_Serialized(body)
                                    } catch (ex: Exception) {
                                        binding.oneSerialItemScanLayout.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                } else {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.please_enter_qty)
                                }
                            } else {
                                binding.oneSerialItemScanLayout.serialNo.error =
                                    getString(R.string.please_scan_serial)
                            }
                        } else {
                            if (selectedMaterial!!.isSerialized) {
                                if (scannedSerialsList.isNotEmpty() && selectedMaterial!!.serials.isNotEmpty()) {
                                    val body =
                                        PutAwayProductionWorkOrder_SerializedBody(
                                            workOrderIssueId = workOrder.workOrderIssueId,
                                            workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                            isBulk = false,
                                            bulkQty = 0,
                                            storageBinCode = binCode,
                                            serials = scannedSerialsList
                                        )
                                    viewModel.putAwayInvoice_Serialized(body)
                                } else {
                                    warningDialog(
                                        requireContext(),
                                        getString(R.string.please_scan_serials))
                                }
                            } else {
                                val putAwayQtyText =
                                    binding.unserializedEnterQtyLayout.countedQty.editText?.text.toString()
                                        .trim()
                                if (putAwayQtyText.isNotEmpty()) {
                                    if (Tools.containsOnlyDigits(putAwayQtyText)) {
                                        val putAwayQty = putAwayQtyText.toInt()
                                        if (putAwayQty <= selectedMaterial!!.receivedQuantity) {
                                            val body = PutAwayProductionWorkOrder_UnserializedBody(
                                                workOrderIssueId = workOrder.workOrderIssueId,
                                                workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                                storageBinCode = binCode,
                                                putAwayQty = putAwayQty
                                            )
                                            viewModel.putAwayInvoice_Unserialized(body)
                                        } else {
                                            binding.unserializedEnterQtyLayout.countedQty.error =
                                                getString(R.string.put_away_qty_must_be_less_or_equal_to) + " ${selectedMaterial!!.receivedQuantity}"
                                        }
                                    } else {
                                        binding.unserializedEnterQtyLayout.countedQty.error =
                                            getString(R.string.put_away_qty_must_contains_only_digits)
                                    }
                                } else {
                                    binding.unserializedEnterQtyLayout.countedQty.error =
                                        getString(R.string.please_enter_put_away_qty)
                                }
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
        selectedMaterial!!.serials.find { it.serial == scannedSerialsList[position].serial }?.isPutaway =
            false
        scannedSerialsList.removeAt(position)
        binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
        scannedSerialsAdapter.notifyDataSetChanged()
    }
}