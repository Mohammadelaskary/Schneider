package net.gbs.schneider.Ui.Issue.StartIssuing

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_UnserializedBody
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.Model.WorkOrderDetails
import net.gbs.schneider.Model.WorkOrderType
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartReceivingFragment
import net.gbs.schneider.WorkOrderDetailsList.WorkOrderDetailsListAdapter
import net.gbs.schneider.WorkOrderDetailsList.WorkOrderDetailsListDialog
import net.gbs.schneider.databinding.FragmentStartIssueingBinding

class StartIssuingFragment :
    BaseFragmentWithViewModel<StartIssuingViewModel, FragmentStartIssueingBinding>(),
    WorkOrderDetailsListAdapter.OnWorkOrderDetailsItemClicked, BarcodeReader.BarcodeListener,
    BarcodeReader.TriggerListener, View.OnClickListener,
    SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = StartReceivingFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartIssueingBinding
        get() = FragmentStartIssueingBinding::inflate
    private lateinit var workOrderDetailsListDialog: WorkOrderDetailsListDialog
    private lateinit var serialsListDialog: IssueSerialsListDialog
    private lateinit var workOrder: WorkOrder
    private lateinit var barcodeReader: net.gbs.schneider.Tools.BarcodeReader
    private var scannedQty = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = net.gbs.schneider.Tools.BarcodeReader(this, this)
    }

    private fun fillData() {
        workOrderDetailsListDialog =
            WorkOrderDetailsListDialog(requireContext(), workOrder.workOrderDetails, this)
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

    private var bulkSerialCode = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workOrder = requireArguments().getString(IntentKeys.WORK_ORDER_KEY)
            ?.let { WorkOrder.fromJson(it) }!!
        binding.serializedScanLayout.serialList.visibility = GONE
        binding.oneSerialItemScanLayout.serialList.visibility = GONE
        fillData()
        observeBinCodeDataAndStatus()
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        clearInputLayoutError(
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
            workOrderDetailsListDialog.show()
        }
        binding.serializedScanLayout.serialNo.setEndIconOnClickListener {
            serialsListDialog.show()
        }
        binding.clearMaterialData.setOnClickListener {
            clearMaterialData()
        }
        binding.materialList.setOnClickListener {
            workOrderDetailsListDialog.show()
        }
        binding.materialCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    binding.clearMaterialData.visibility = VISIBLE
                    binding.materialList.visibility = INVISIBLE
                } else {
                    binding.clearMaterialData.visibility = INVISIBLE
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
            bulkSerialCode = ""
            binding.oneSerialItemScanLayout.serialNo.isEnabled = true
        }


        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            for (material in workOrder.workOrderDetails) {
                if (materialCode == material.materialCode) {
                    selectedWorkOrderDetails = material
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
            if (scannedSerialsList.size<selectedWorkOrderDetails?.remainingQty!!) {
                val issuedSerial =
                    selectedWorkOrderDetails?.issuedSerials?.find { it.serial == serialNo }
                if (issuedSerial == null) {
                    val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                    if (scannedSerial == null) {
                        val serial = Serial(
                            isReceived = true,
                            serial = serialNo,
                        )
//                        serial.isReceived = true
                        scannedSerialsList.add(serial)
                        binding.serializedScanLayout.serialNo.editText?.setText("")
                        scannedSerialsAdapter.notifyDataSetChanged()

                        binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
                        serialsListDialog.setSerialAsScanned(serial)
                    } else warningDialog(
                        requireContext(),
                        getString(R.string.this_serial_is_issued_before)
                    )
                } else {
                    warningDialog(
                        requireContext(),
                        getString(R.string.serial_added_before)
                    )
                }
            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.all_qty_are_scanned)
                )
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
            if (serialNo.isNotEmpty()) {
//                val serial = selectedWorkOrderDetails!!.issuedSerials.find { it.serial==serialNo }
//                if (serial == null){
                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                    1
                } else {
                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                }
                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                if (scannedQty > selectedWorkOrderDetails!!.workOrderDetailQuantity){
                    binding.oneSerialItemScanLayout.countedQty.error =
                        getString(R.string.max_quantity_is)+ selectedWorkOrderDetails!!.workOrderDetailQuantity
                } else if (scannedQty == selectedWorkOrderDetails!!.workOrderDetailQuantity) {
                    if (isMax){
                        warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                    }
                    isMax = true
                }
//                } else {
//                    binding.oneSerialItemScanLayout.serialNo.error = getString(R.string.this_serial_is_issued_before)
//                }
            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.please_scan_serial)
                )
            }
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
                    warningDialog(requireContext(), it.message)
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
            if (selectedWorkOrderDetails?.isSerializedWithOneSerial!!) {
                showOneSerialLayout()
            } else {
                if (selectedWorkOrderDetails?.isSerialized == true) {
                    showSerializedLayout()
                    binding.serializedScanLayout.scannedQty.text = "${scannedSerialsList.size}"
                    serialsListDialog =
                        IssueSerialsListDialog(requireContext(), selectedWorkOrderDetails!!)
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
                    showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.resultWorkOrder.observe(requireActivity()) {
            workOrder = it
            if (workOrder.workOrderDetails.isEmpty())
                back(this)
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
        scannedSerialsList.clear()
        bulkSerialCode = ""
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

    private var selectedWorkOrderDetails: WorkOrderDetails? = null

    override fun onWorkOrderDetailsItemClicked(workOrderDetails: WorkOrderDetails) {
        selectedWorkOrderDetails = workOrderDetails
        fillMaterialData()
        workOrderDetailsListDialog.dismiss()
    }

    private var isMax = false
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (getEditTextText(binding.materialCode.editText!!).isEmpty()) {
                for (material in workOrder.workOrderDetails) {
                    if (scannedText == material.materialCode) {
                        selectedWorkOrderDetails = material
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
                        if (scannedSerialsList.size<selectedWorkOrderDetails?.remainingQty!!) {
                            val issuedSerial =
                                selectedWorkOrderDetails?.issuedSerials?.find { it.serial == serialNo }
                            if (issuedSerial == null) {
                                val scannedSerial =
                                    scannedSerialsList.find { it.serial == serialNo }
                                if (scannedSerial == null) {
                                    val serial = Serial(
                                        serial = serialNo,
                                    )
//                        serial.isReceived = true
                                    scannedSerialsList.add(serial)
                                    scannedSerialsAdapter.notifyDataSetChanged()
                                    binding.serializedScanLayout.scannedQty.text =
                                        "${scannedSerialsList.size}"
                                    serialsListDialog.setSerialAsScanned(serial)
                                } else warningDialog(
                                    requireContext(),
                                    getString(R.string.this_serial_is_issued_before)
                                )
                            } else {
                                warningDialog(
                                    requireContext(),
                                    getString(R.string.serial_added_before)
                                )
                            }
                        } else {
                            warningDialog(
                                requireContext(),
                                getString(R.string.all_qty_are_scanned)
                            )
                        }
//                } else {
//                    binding.serializedScanLayout?.serialNo?.error = getString(R.string.wrong_serial_no)
//                }
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.please_scan_serial)
                        )
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
                            if (scannedQty > selectedWorkOrderDetails!!.workOrderDetailQuantity){
                                binding.oneSerialItemScanLayout.countedQty.error =
                                    getString(R.string.max_quantity_is)+ selectedWorkOrderDetails!!.workOrderDetailQuantity
                            } else if (scannedQty == selectedWorkOrderDetails!!.workOrderDetailQuantity) {
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
                                if (scannedQty > selectedWorkOrderDetails!!.workOrderDetailQuantity) {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is) + selectedWorkOrderDetails!!.workOrderDetailQuantity
                                } else if (scannedQty < selectedWorkOrderDetails!!.workOrderDetailQuantity) {
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
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.please_scan_serial)
                        )
                    }
                }
            }
        }
    }

    private fun fillMaterialData() {
        with(binding) {
            materialCode.editText?.setText(selectedWorkOrderDetails?.materialCode)
            materialDesc.text = selectedWorkOrderDetails?.materialName
            issuedPerTotal.text =
                "${selectedWorkOrderDetails?.issuedQuantity}/${selectedWorkOrderDetails?.workOrderDetailQuantity}"
            projectFrom.text = selectedWorkOrderDetails?.projectNumberFrom
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
                if (selectedWorkOrderDetails != null) {
                    if (selectedWorkOrderDetails!!.isSerializedWithOneSerial) {
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
                                    Log.d(TAG, "onClickQty: $qtyText")
                                    Log.d(TAG, "onClickEx: ${ex.message}")
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.please_enter_valid_quantity)
                                }
                                if (qty <= selectedWorkOrderDetails!!.workOrderDetailQuantity) {
                                    val issueWorkOrder_SerializedBody =
                                        IssueWorkOrder_SerializedBody(
                                            WorkOrderIssueDetailsId = selectedWorkOrderDetails?.workOrderIssueDetailsId!!,
                                            WorkOrderIssueId = workOrder.workOrderIssueId!!,
                                            serials = listOf(
                                                Serial(
                                                    serial = serialNo,
                                                )
                                            ),
                                            IsBulk = true,
                                            BulkQty = qty
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
                        if (selectedWorkOrderDetails!!.isSerialized) {
                            if (scannedSerialsList.isNotEmpty()) {
                                val issueWorkOrder_SerializedBody = IssueWorkOrder_SerializedBody(
                                    WorkOrderIssueDetailsId = selectedWorkOrderDetails?.workOrderIssueDetailsId!!,
                                    WorkOrderIssueId = workOrder.workOrderIssueId!!,
                                    serials = scannedSerialsList,
                                    IsBulk = false,
                                    BulkQty = 0
                                )
                                viewModel.issueWorkOrderSerialized(issueWorkOrder_SerializedBody)
                            } else {
                                warningDialog(
                                    requireContext(),
                                    getString(R.string.please_scan_serials)
                                )
                            }
                        } else {
                            if (binding.binCode.editText?.text?.isNotEmpty() == true) {
                                val issuedQtyText =
                                    binding.unserializedEnterQtyLayout.countedQty.editText?.text.toString()
                                        .trim()
                                if (issuedQtyText.isNotEmpty()) {
                                    if (Tools.containsOnlyDigits(issuedQtyText)) {
                                        val issuedQty = issuedQtyText.toInt()
                                        if (issuedQty <= selectedWorkOrderDetails?.workOrderDetailQuantity!!) {
                                            val issueWorkOrder_UnserializedBody =
                                                IssueWorkOrder_UnserializedBody(
                                                    WorkOrderIssueDetailsId = selectedWorkOrderDetails?.workOrderIssueDetailsId!!,
                                                    WorkOrderIssueId = workOrder.workOrderIssueId!!,
                                                    issuedQty = issuedQty,
                                                    StorageBinCode = binding.binCode.editText?.text?.toString()
                                                        ?.trim()!!
                                                )
                                            viewModel.issueWorkOrderUnserialized(
                                                issueWorkOrder_UnserializedBody
                                            )
                                        } else {
                                            binding.unserializedEnterQtyLayout.countedQty.error =
                                                getString(R.string.issue_quantity_must_be_less_or_equal_to) + " ${selectedWorkOrderDetails?.workOrderDetailQuantity}"
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
}