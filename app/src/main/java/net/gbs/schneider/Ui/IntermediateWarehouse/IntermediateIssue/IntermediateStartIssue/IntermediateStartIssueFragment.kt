package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.IntermediateIssueWorkOrder
import net.gbs.schneider.Model.IntermediateIssueWorkOrderDetails
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.WorkOrderType
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue.IntermediateIssueMaterialsListDialog.IntermediateIssueMaterialsListAdapter
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue.IntermediateIssueMaterialsListDialog.IntermediateIssueMaterialsListDialog
import net.gbs.schneider.databinding.FragmentIntermediateStartIssueBinding

class IntermediateStartIssueFragment :
    BaseFragmentWithViewModel<IntermediateStartIssueViewModel, FragmentIntermediateStartIssueBinding>(),
    BarcodeListener, TriggerListener, OnClickListener,
    IntermediateIssueMaterialsListAdapter.OnIntermediateMaterialsItemClicked,
    SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = IntermediateStartIssueFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateStartIssueBinding
        get() = FragmentIntermediateStartIssueBinding::inflate

    private lateinit var intermediateMaterialsListDialog: IntermediateIssueMaterialsListDialog
    private lateinit var serialsListDialog: IntermediateIssueSerialsListDialog
    private lateinit var workOrder: IntermediateIssueWorkOrder
    private lateinit var barcodeReader: BarcodeReader
    private var scannedQty = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {

        intermediateMaterialsListDialog =
            IntermediateIssueMaterialsListDialog(requireContext(), workOrder.workOrderDetails, this)
        binding.let {
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.header.workOrderNumber.text = workOrder.workOrderNumber
            it.header.projectTo.text = workOrder.projectNumberTo
            it.header.workOrderType.text =
                if (workOrder.workOrderType == WorkOrderType.PRODUCTION)
                    getString(R.string.production)
                else
                    getString(R.string.spare_parts)
//            it?.header?.projectDesc?.text = workOrder.projectDesc
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workOrder = requireArguments().getString(IntentKeys.WORK_ORDER_KEY)
            ?.let { IntermediateIssueWorkOrder.fromJson(it) }!!
        binding.serializedScanLayout.serialList.visibility = GONE
        binding.oneSerialItemScanLayout.serialList.visibility = GONE
        fillData()
        observeBinCodeDataAndStatus()
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        Tools.clearInputLayoutError(
            binding.serializedScanLayout.serialNo,
            binding.materialCode,
            binding.binCode,
            binding.unserializedEnterQtyLayout.countedQty,
            binding.serializedScanLayout.serialNo,
            binding.oneSerialItemScanLayout.serialNo,
            binding.oneSerialItemScanLayout.countedQty
        )
        setUpScannedSerialRecyclerView()
        binding.materialCode.setEndIconOnClickListener {
            intermediateMaterialsListDialog.show()
        }
        binding.serializedScanLayout.serialNo.setEndIconOnClickListener {
            serialsListDialog.show()
        }
        binding.clearMaterialData.setOnClickListener {
            clearMaterialData()
        }
        binding.materialList.setOnClickListener {
            intermediateMaterialsListDialog.show()
        }
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
        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            for (material in workOrder.workOrderDetails) {
                if (materialCode == material.materialCode) {
                    selectedMaterial = material
                    fillMaterialData()
                    break
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            }
        }

        EditTextActionHandler.OnEnterKeyPressed(binding.binCode) {
            val bin = binding.binCode.editText?.text.toString()
            if (bin.isNotEmpty())
                viewModel.getBinCodeData(bin)
            else
                binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
        }

        EditTextActionHandler.OnEnterKeyPressed(binding.serializedScanLayout.serialNo) {
            val serialNo = binding.serializedScanLayout.serialNo.editText?.text.toString().trim()
            val issuedSerial = selectedMaterial?.issuedSerials?.find { it.serial == serialNo }
            if (issuedSerial == null) {
//                    val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
//                    if (scannedSerial==null) {
                val serial = Serial(
                    isReceived = true,
                    serial = serialNo,
                )
                binding.serializedScanLayout.serialNo.editText?.setText("")
//                        serial.isReceived = true
                scannedSerialsList.add(serial)
                scannedSerialsAdapter.notifyDataSetChanged()

                binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
                serialsListDialog.setSerialAsScanned(serial)
            } else Tools.warningDialog(
                requireContext(),
                getString(R.string.this_serial_is_issued_before)
            )
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = binding.serializedScanLayout.serialNo.editText?.text.toString()
//            val serial = selectedMaterial?.issuedSerials?.find { it.serial == serialNo }
//            Log.d(TAG, "onViewCreated: ${serial?.serial}")
//            if (serial != null) {
            binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
            scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                1
            } else {
                getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
            }
            binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
            if (scannedQty > selectedMaterial!!.workOrderDetailQuantity){
                binding.oneSerialItemScanLayout.countedQty.error =
                    getString(R.string.max_quantity_is)+ selectedMaterial!!.workOrderDetailQuantity
            } else if (scannedQty == selectedMaterial!!.workOrderDetailQuantity) {
                if (isMax){
                    warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                }
                isMax = true
            }
//            } else {
//                binding.serializedScanLayout.serialNo.error =
//                    getString(R.string.wrong_serial_no)
//            }
        }
        observeIssuingWorkOrder()
    }

    private fun observeBinCodeDataAndStatus() {
        viewModel.getBinCodeStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS, Status.IDLE -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.binCode.error = it.message
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getBinCodeData.observe(requireActivity()) {
            val binCode = it.storageBinCode
            val binData =
                "${it.warehouseName} -> ${it.storageLocationName} -> ${it.storageSectionCode} -> ${it.storageBinCode}"
            binding.binCode.editText?.setText(binCode)
            binding.locationLayout.visibility = VISIBLE
            binding.locationDetails.text = binData
            binding.save.visibility = VISIBLE
            if (selectedMaterial?.isSerializedWithOneSerial!!) {
                showOneSerialLayout()
            } else {
                if (selectedMaterial?.isSerialized == true) {
                    showSerializedLayout()
                    binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
                    serialsListDialog =
                        IntermediateIssueSerialsListDialog(requireContext(), selectedMaterial!!)
                } else {
                    showUnSerializedLayout()
                }
            }
        }
    }

    private fun observeIssuingWorkOrder() {
        viewModel.resultWorkOrderStatus.observe(requireActivity()) {
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
        viewModel.resultWorkOrder.observe(requireActivity()) {
            workOrder = it
            if (workOrder.workOrderDetails.isEmpty())
                Tools.back(this)
            else {
                fillData()
                clearMaterialData()
            }
        }
    }

    private fun clearMaterialData() {
        binding.binCode.editText?.setText("")
        binding.locationLayout.visibility = GONE
        binding.materialDataGroup.visibility = GONE
        binding.save.visibility = GONE
        binding.locationLayout.visibility = GONE
        binding.serializedScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.serializedScanLayout.serialNo.editText?.setText("")
        scannedQty = 0
        scannedSerialsList.clear()
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        scannedSerialsAdapter.notifyDataSetChanged()

    }

    private lateinit var scannedSerialsAdapter: SerialsWithRemoveButtonAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        scannedSerialsAdapter = SerialsWithRemoveButtonAdapter(scannedSerialsList, this)
        binding.serializedScanLayout.scannedSerials.adapter = scannedSerialsAdapter
    }

    private var selectedMaterial: IntermediateIssueWorkOrderDetails? = null

    private var isMax = false
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (getEditTextText(binding.materialCode.editText!!).isEmpty()) {
                for (material in workOrder.workOrderDetails) {
                    if (scannedText == material.materialCode) {
                        selectedMaterial = material
                        Log.d(
                            "IntermediateStartIssue",
                            "observeBinCodeDataAndStatusSerialized: ${selectedMaterial?.isSerializedWithOneSerial!!}"
                        )
                        Log.d(
                            "IntermediateStartIssue",
                            "observeBinCodeDataAndStatusBulk: ${selectedMaterial?.isSerializedWithOneSerial!!}"
                        )
                        Log.d(
                            "IntermediateStartIssue",
                            "observeBinCodeDataAndStatusSerializedBulk: ${selectedMaterial?.isSerializedWithOneSerial!!}"
                        )
                        fillMaterialData()
                        break
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }
                }
            } else if (getEditTextText(binding.binCode.editText!!).isEmpty()) {
                viewModel.getBinCodeData(scannedText)
            } else {
                if (binding.serializedScanLayout.root.visibility == VISIBLE) {
                    val serialNo = barcodeReader.scannedData(p0)
                    if (serialNo.isNotEmpty()) {
                        val issuedSerial =
                            selectedMaterial?.issuedSerials?.find { it.serial == serialNo }
                        if (issuedSerial == null) {
//                    val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
//                    if (scannedSerial==null) {
                            val serial = Serial(
                                serial = serialNo,
                            )
//                        serial.isReceived = true
                            scannedSerialsList.add(serial)
                            scannedSerialsAdapter.notifyDataSetChanged()
                            binding.serializedScanLayout.scannedQty.text =
                                "${scannedSerialsList.size}"
                            serialsListDialog.setSerialAsScanned(serial)
                        } else Tools.warningDialog(
                            requireContext(),
                            getString(R.string.this_serial_is_issued_before)
                        )
//                    } else {
//                        binding.serializedScanLayout?.serialNo?.error = getString(R.string.serial_added_before)
//                    }
//                } else {
//                    binding.serializedScanLayout?.serialNo?.error = getString(R.string.wrong_serial_no)
//                }
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.please_scan_serial))
                    }
                } else if (binding.oneSerialItemScanLayout.root.visibility == VISIBLE) {
                    val serialNo = barcodeReader.scannedData(p0)
                    if (serialNo.isNotEmpty()) {
                        val currentSerial = getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!)
                        if (currentSerial.isEmpty()){
                                binding.oneSerialItemScanLayout.serialNo.editText?.setText(serialNo)
                                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                                    1
                                } else {
                                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                                }
                                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                                if (scannedQty > selectedMaterial!!.workOrderDetailQuantity){
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is)+ selectedMaterial!!.workOrderDetailQuantity
                                } else if (scannedQty == selectedMaterial!!.workOrderDetailQuantity) {
                                    if (isMax){
                                        warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                                    }
                                    isMax = true
                                }
                        } else {
                            if (serialNo == currentSerial){
                                scannedQty =
                                    if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()) {
                                        1
                                    } else {
                                        getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                                    }
                                if (scannedQty > selectedMaterial!!.workOrderDetailQuantity) {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is) + selectedMaterial!!.workOrderDetailQuantity
                                } else if (scannedQty < selectedMaterial!!.workOrderDetailQuantity) {
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
                            } else {
                                warningDialog(requireContext(),getString(R.string.please_scan_same_serial))
                            }
                        }
//                        val serial = selectedWorkOrderDetails!!.issuedSerials.find { it.serial==serialNo }
//                        if (serial != null){
//                        val currentSerial =
//                            binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
//                                .trim()
//                        if (currentSerial.isEmpty()) {
//                            binding.oneSerialItemScanLayout.serialNo.editText?.setText(serialNo)
//                            scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
//                                0
//                            }else{
//                                getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
//                            }
//                            if (scannedQty < selectedMaterial!!.remainingQty) {
//                                scannedQty++
//                                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
//                                binding.oneSerialItemScanLayout.countedQty.editText?.setText(
//                                    scannedQty.toString()
//                                )
//                            } else {
//                                binding.oneSerialItemScanLayout.countedQty.error =
//                                    getString(R.string.all_qty_are_scanned)
//                            }
////                        } else {
////                            binding.oneSerialItemScanLayout?.serialNo?.error = getString(R.string.wrong_serial_no)
////                        }
//                        } else {
//                            if (currentSerial == serialNo) {
//                                binding.oneSerialItemScanLayout.serialNo.editText?.setText(serialNo)
//                                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
//                                    0
//                                }else{
//                                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
//                                }
//                                if (scannedQty < selectedMaterial!!.remainingQty) {
//                                    scannedQty++
//                                    binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled =
//                                        false
//                                    binding.oneSerialItemScanLayout.countedQty.editText?.setText(
//                                        scannedQty.toString()
//                                    )
//                                } else {
//                                    binding.oneSerialItemScanLayout.countedQty.error =
//                                        getString(R.string.all_qty_are_scanned)
//                                }
//                            } else {
//                                binding.oneSerialItemScanLayout.serialNo.error =
//                                    getString(R.string.please_scan_same_serial)
//                            }
//                        }
                    } else {
                        binding.oneSerialItemScanLayout.serialNo.error =
                            getString(R.string.please_scan_serial)
                    }
                }
            }
        }
    }

    private fun fillMaterialData() {
        with(binding) {
            materialCode.editText?.setText(selectedMaterial?.materialCode)
            materialDesc.text = selectedMaterial?.materialName
            issuedPerTotal.text =
                "${selectedMaterial?.issuedQuantity}/${selectedMaterial?.workOrderDetailQuantity}"
            projectFrom.text = selectedMaterial?.projectNumberFrom
            materialDataGroup.visibility = VISIBLE
        }
    }

    private fun showSerializedLayout() {
        binding.serializedScanLayout.root.visibility = VISIBLE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
    }

    private fun showUnSerializedLayout() {
        binding.serializedScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = VISIBLE
        binding.oneSerialItemScanLayout.root.visibility = GONE
    }

    private fun showOneSerialLayout() {
        binding.serializedScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = VISIBLE
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }

    override fun onResume() {
        super.onResume()
        Tools.changeTitle(getString(R.string.start_issue), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if (selectedMaterial != null) {
                    if (selectedMaterial!!.isSerializedWithOneSerial) {
                        val serialNo =
                            binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
                                .trim()
                        if (serialNo.isNotEmpty()) {
                            val qtyText =
                                binding.oneSerialItemScanLayout.countedQty.editText?.text.toString()
                                    .trim()
                            if (qtyText.isNotEmpty()) {
                                var qty = 0
                                try {
                                    qty = qtyText.toInt()
                                } catch (ex: Exception) {
                                    Log.d(ContentValues.TAG, "onClickQty: $qtyText")
                                    Log.d(ContentValues.TAG, "onClickEx: ${ex.message}")
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.please_enter_valid_quantity)
                                }
                                if (qty <= selectedMaterial!!.remainingQty!!) {
                                    val issueWorkOrder_SerializedBody =
                                        IssueProductionWorkOrder_SerializedBody(
                                            workOrderIssueDetailsId = selectedMaterial?.workOrderIssueDetailsId!!,
                                            workOrderIssueId = workOrder.workOrderIssueId!!,
                                            serials = listOf(
                                                Serial(
                                                    serial = serialNo,
                                                )
                                            ),
                                            isBulk = true,
                                            bulkQty = qty
                                        )
                                    viewModel.issueWorkOrderSerialized(issueWorkOrder_SerializedBody)
                                } else {
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
                            if (scannedSerialsList.isNotEmpty()) {
                                val issueWorkOrder_SerializedBody =
                                    IssueProductionWorkOrder_SerializedBody(
                                        workOrderIssueDetailsId = selectedMaterial?.workOrderIssueDetailsId!!,
                                        workOrderIssueId = workOrder.workOrderIssueId!!,
                                        serials = scannedSerialsList,
                                        isBulk = false,
                                        bulkQty = 0
                                    )
                                viewModel.issueWorkOrderSerialized(issueWorkOrder_SerializedBody)
                            } else {
                                warningDialog(
                                    requireContext(),
                                    getString(R.string.please_scan_serials))
                            }
                        } else {
                            if (binding.binCode.editText?.text?.isNotEmpty() == true) {
                                val issuedQtyText =
                                    binding.unserializedEnterQtyLayout.countedQty.editText?.text.toString()
                                        .trim()
                                if (issuedQtyText.isNotEmpty()) {
                                    if (Tools.containsOnlyDigits(issuedQtyText)) {
                                        val issuedQty = issuedQtyText.toInt()
                                        if (issuedQty <= selectedMaterial?.workOrderDetailQuantity!!) {
                                            val issueWorkOrder_UnserializedBody =
                                                IssueProductionWorkOrder_UnserializedBody(
                                                    workOrderIssueDetailsId = selectedMaterial?.workOrderIssueDetailsId!!,
                                                    workOrderIssueId = workOrder.workOrderIssueId!!,
                                                    issuedQty = issuedQty,
                                                    storageBinCode = binding.binCode.editText?.text?.toString()
                                                        ?.trim()!!
                                                )
                                            viewModel.issueWorkOrderUnserialized(
                                                issueWorkOrder_UnserializedBody
                                            )
                                        } else {
                                            binding.unserializedEnterQtyLayout.countedQty.error =
                                                getString(R.string.issue_quantity_must_be_less_or_equal_to) + " ${selectedMaterial?.workOrderDetailQuantity}"
                                        }
                                    } else {
                                        binding.unserializedEnterQtyLayout.countedQty.error =
                                            getString(R.string.please_enter_issued_quantity)
                                    }
                                } else {
                                    binding.unserializedEnterQtyLayout.countedQty.error =
                                        getString(R.string.please_enter_issued_quantity)
                                }
                            } else {
                                binding.binCode.error =
                                    getString(R.string.please_scan_or_enter_bin_code)
                            }
                        }
                    }
                } else {
                    binding.materialCode.error =
                        getString(R.string.please_scan_or_select_material_first)
                }
            }
        }
    }

    override fun OnRemovedSerial(position: Int) {
        scannedSerialsList.removeAt(position)
        binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
        scannedSerialsAdapter.notifyDataSetChanged()
    }

    override fun onIntermediateMaterialsItemClicked(material: IntermediateIssueWorkOrderDetails) {
        selectedMaterial = material
        fillMaterialData()
        intermediateMaterialsListDialog.dismiss()
    }
}