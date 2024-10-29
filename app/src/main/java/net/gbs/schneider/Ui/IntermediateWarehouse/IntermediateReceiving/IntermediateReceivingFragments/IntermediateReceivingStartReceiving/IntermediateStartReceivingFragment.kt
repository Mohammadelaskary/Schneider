package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.showToolBar
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving.IntermediateMaterialsListDialog.IntermediateMaterialsListAdapter
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving.IntermediateMaterialsListDialog.IntermediateMaterialsListDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg.IntermediateSerialsListDialog
import net.gbs.schneider.databinding.FragmentIntermdiateStartReceivingBinding

class IntermediateStartReceivingFragment :
    BaseFragmentWithViewModel<IntermediateStartReceivingViewModel, FragmentIntermdiateStartReceivingBinding>(),
    BarcodeListener, TriggerListener,
    IntermediateMaterialsListAdapter.OnIntermediateMaterialsItemClicked, OnClickListener,
    SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = IntermediateStartReceivingFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermdiateStartReceivingBinding
        get() = FragmentIntermdiateStartReceivingBinding::inflate

    private lateinit var workOrder: IntermediateWorkOrder
    private var bulkSerialCode = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
        binding.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
        if (arguments != null) {
            workOrder = IntermediateWorkOrder.fromJson(
                requireArguments().getString(WORK_ORDER_KEY).toString()
            )
            fillHeaderData()
        }
        clearInputLayoutError(
            binding.materialCode,
            binding.oneSerialItemScanLayout.serialNo,
            binding.oneSerialItemScanLayout.countedQty,
            binding.serializedScanLayout.serialNo,
            binding.unserializedEnterQtyLayout.countedQty
        )
        attachButtonsToListener(
            this,
            binding.materialList,
            binding.clearMaterialData,
            binding.save,
        )
        binding.serializedScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }
        setUpSerialsRecyclerView()
        binding.oneSerialItemScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }
        binding.oneSerialItemScanLayout.clearSerial.setOnClickListener {
            binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
            binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
            scannedQty = 0
            bulkSerialCode = ""
            binding.oneSerialItemScanLayout.serialNo.isEnabled = true
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            if (getEditTextText(binding.materialCode.editText!!).isNotEmpty()) {
                val workOrderDetails =
                    workOrder.materials.find { it.materialCode == getEditTextText(binding.materialCode.editText!!) }
                if (workOrderDetails != null) {
                    selectedMaterial = workOrderDetails
                    fillMaterialData()
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            } else {
                binding.materialCode.error = getString(R.string.please_scan_or_enter_material_code)
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.serializedScanLayout.serialNo) {
            val serialNo = getEditTextText(binding.materialCode.editText!!)
            val scannedSerial =
                selectedMaterial?.serials?.find { it.serial == serialNo }
            if (scannedSerial != null) {
                if (!scannedSerial.isReceived) {
                    if (!scannedSerial.isPutaway) {
                        val alreadyScannedSerial =
                            scannedSerials.find { it.serial == serialNo }
                        if (alreadyScannedSerial == null) {
                            scannedSerial.isReceived = true
                            binding.serializedScanLayout.serialNo.editText?.setText("")
                            scannedSerials.add(scannedSerial)
                            serialsAdapter.notifyDataSetChanged()
                        } else {
                            warningDialog(
                                requireContext(),
                                getString(R.string.serial_already_scanned))
                        }
                    } else {
                        warningDialog(
                                            requireContext(),
                            getString(R.string.serial_already_put_away))
                    }
                } else {
                    warningDialog(
                                            requireContext(),
                        getString(R.string.serial_already_received))
                }
            } else {
                warningDialog(
                                            requireContext(),
                    getString(R.string.wrong_serial_no))
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!)
                val scannedSerial =
                    selectedMaterial!!.serials.find { it.serial == serialNo }
                if (scannedSerial != null) {
                    binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                    scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                        1
                    } else {
                        getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                    }
                    binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                    if (scannedQty > selectedMaterial!!.remainingQty){
                        binding.oneSerialItemScanLayout.countedQty.error =
                            getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingQty
                    } else if (scannedQty == selectedMaterial!!.remainingQty) {
                        if (isMax){
                            warningDialog(requireContext(),getString(R.string.all_qty_are_scanned))
                        }
                        isMax = true
                    }

                } else {
                    binding.oneSerialItemScanLayout.serialNo.error =
                        getString(R.string.wrong_serial_no)
                }

        }
        watchItemCodeText()
        observeSavingResult()
    }

    private fun watchItemCodeText() {
        binding.materialCode.editText?.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                binding.clearMaterialData.visibility = VISIBLE
                binding.materialList.visibility = INVISIBLE
            } else {
                binding.clearMaterialData.visibility = INVISIBLE
                binding.materialList.visibility = VISIBLE
            }
        }
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

    }

    private fun observeSavingResult() {
        viewModel.receiveStatus.observe(viewLifecycleOwner) {
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
        viewModel.receiveResponse.observe(viewLifecycleOwner) {
            workOrder = it
            fillHeaderData()
            clearMaterialData()
            if (workOrder.materials.isEmpty())
                back(this)
        }
    }

    private lateinit var serialsAdapter: SerialsWithRemoveButtonAdapter
    private val scannedSerials = mutableListOf<Serial>()
    private fun setUpSerialsRecyclerView() {
        serialsAdapter = SerialsWithRemoveButtonAdapter(scannedSerials, this)
        binding.serializedScanLayout.scannedSerials.adapter = serialsAdapter
    }

    private lateinit var serialsListDialog: IntermediateSerialsListDialog
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private lateinit var materialListDialog: IntermediateMaterialsListDialog
    private fun fillHeaderData() {
        binding.header.workOrderNumber.text = workOrder.workOrderNumber
        binding.header.projectTo.text = workOrder.projectNumber
        materialListDialog =
            IntermediateMaterialsListDialog(requireContext(), workOrder.materials, this)
    }

    private var scannedQty = 0
    private var isMax = false
    override fun onBarcodeEvent(p: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p!!)
            if (getEditTextText(binding.materialCode.editText!!).isEmpty()) {
                val workOrderDetails = workOrder.materials.find { it.materialCode == scannedText }
                if (workOrderDetails != null) {
                    selectedMaterial = workOrderDetails
                    fillMaterialData()
                } else binding.materialCode.error = getString(R.string.wrong_material_code)
            } else {
                if (binding.oneSerialItemScanLayout.root.visibility == VISIBLE) {
                        val scannedSerial =
                            selectedMaterial!!.serials.find { it.serial == scannedText }
                        if (scannedSerial != null) {
                            if (getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!).isEmpty()){
                                binding.oneSerialItemScanLayout.serialNo.editText?.setText(scannedText)
                                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                                    1
                                } else {
                                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                                }
                                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                                if (scannedQty > selectedMaterial!!.remainingQty){
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingQty
                                } else if (scannedQty == selectedMaterial!!.remainingQty) {
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
                                if (scannedQty > selectedMaterial!!.remainingQty) {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQty
                                } else if (scannedQty < selectedMaterial!!.remainingQty) {
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
                        } else {
                            binding.oneSerialItemScanLayout.serialNo.error =
                                getString(R.string.wrong_serial_no)
                        }

                } else if (binding.serializedScanLayout.root.visibility == VISIBLE) {
                    val scannedSerial =
                        selectedMaterial!!.serials.find { it.serial == scannedText }
                    if (scannedSerial != null) {
                        if (!scannedSerial.isReceived) {
                            if (!scannedSerial.isPutaway) {
                                val alreadyScannedSerial =
                                    scannedSerials.find { it.serial == scannedText }
                                if (alreadyScannedSerial == null) {
                                    scannedSerial.isReceived = true
                                    scannedSerials.add(scannedSerial)
                                    binding.serializedScanLayout.scannedQty.text =
                                        "${scannedSerials.size}"
                                    serialsAdapter.notifyDataSetChanged()
                                    binding.serializedScanLayout.serialNo.error = null
                                } else {
                                    warningDialog(
                                            requireContext(),
                                        getString(R.string.serial_already_scanned))
                                }
                            } else {
                                warningDialog(
                                            requireContext(),
                                    getString(R.string.serial_already_put_away))
                            }
                        } else {
                            warningDialog(
                                            requireContext(),
                                getString(R.string.serial_already_received))
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

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p!!)
    }

    private var selectedMaterial: IntermediateMaterial? = null


    private fun fillMaterialData() {
        binding.materialDataGroup.visibility = VISIBLE
        binding.materialCode.editText?.setText(selectedMaterial!!.materialCode)
        binding.materialDesc.text = selectedMaterial!!.materialName
        val receivedPerIssued =
            "${selectedMaterial!!.receivedQuantity} / ${selectedMaterial!!.issuedQuantity}"
        binding.receivedQtyPerIssuedQty.text = receivedPerIssued
        if (selectedMaterial!!.isSerializedWithOneSerial) {
            showOneSerialLayout()
            serialsListDialog = IntermediateSerialsListDialog(requireContext(), selectedMaterial!!)
        } else {
            if (selectedMaterial!!.isSerialized) {
                showSerializedLayout()
                serialsListDialog =
                    IntermediateSerialsListDialog(requireContext(), selectedMaterial!!)
                binding.serializedScanLayout.scannedQty.text = "${scannedSerials.size}"
            } else {
                showUnSerializedLayout()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeReader.onResume()
        showToolBar(activity as MainActivity)
        changeTitle(getString(R.string.start_receiving), requireActivity() as MainActivity)
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
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

    override fun onIntermediateMaterialsItemClicked(material: IntermediateMaterial) {
        selectedMaterial = material
        fillMaterialData()
        materialListDialog.dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.material_list -> materialListDialog.show()
            R.id.clear_material_data -> clearMaterialData()
            R.id.save -> {
                if (selectedMaterial != null) {
                    if (selectedMaterial!!.isSerializedWithOneSerial) {
                        val serialNo =
                            getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!)
                        if (serialNo.isNotEmpty()) {
                            val qty =
                                getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!)
                            if (qty.isNotEmpty()) {
                                try {
                                    if (qty.toInt() <= selectedMaterial!!.remainingQty) {
                                        viewModel.receiveSerialized(
                                            ReceiveProductionWorkOrder_SerializedBody(
                                                workOrderIssueId = workOrder.workOrderIssueId,
                                                workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                                bulkQty = qty.toInt(),
                                                serials = listOf(
                                                    Serial(
                                                        serial = serialNo,
                                                        isReceived = true
                                                    )
                                                ),
                                                isBulk = true
                                            )
                                        )
                                    } else binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.please_enter_valid_quantity)
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
                            if (scannedSerials.isNotEmpty()) {
                                viewModel.receiveSerialized(
                                    ReceiveProductionWorkOrder_SerializedBody(
                                        workOrderIssueId = workOrder.workOrderIssueId,
                                        isBulk = false,
                                        serials = scannedSerials,
                                        bulkQty = 0,
                                        workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                    )
                                )
                            } else {
                                warningDialog(
                                    requireContext(),
                                    getString(R.string.please_scan_received_serials)
                                )
                            }
                        } else {
                            val unserializedQty =
                                getEditTextText(binding.unserializedEnterQtyLayout.countedQty.editText!!)
                            if (unserializedQty.isNotEmpty()) {
                                try {
                                    if (unserializedQty.toInt() <= selectedMaterial!!.remainingQty) {
                                        viewModel.receiveUnserialized(
                                            ReceiveProductionWorkOrder_UnserializedBody(
                                                workOrderIssueId = workOrder.workOrderIssueId,
                                                workOrderIssueDetailsId = selectedMaterial!!.workOrderIssueDetailsId,
                                                receivedQty = unserializedQty.toInt()
                                            )
                                        )
                                    } else {
                                        binding.unserializedEnterQtyLayout.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                } catch (ex: Exception) {
                                    binding.unserializedEnterQtyLayout.countedQty.error =
                                        getString(R.string.please_enter_valid_quantity)
                                }
                            } else {
                                binding.unserializedEnterQtyLayout.countedQty.error =
                                    getString(R.string.please_enter_qty)
                            }
                        }
                    }
                } else {
                    binding.materialCode.error =
                        getString(R.string.please_scan_or_enter_material_code)
                }
            }
        }
    }

    private fun clearMaterialData() {
        selectedMaterial = null
        binding.materialCode.editText?.setText("")
        binding.materialDataGroup.visibility = GONE
        scannedQty = 0
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        binding.serializedScanLayout.root.visibility = GONE
        binding.serializedScanLayout.serialNo.editText?.setText("")
        scannedSerials.forEach { scannedSerial ->
            selectedMaterial?.serials?.forEach{ serial ->
                if (serial.serial==scannedSerial.serial){
                    serial.isReceived = false
                }
            }
        }
        scannedSerials.clear()
        bulkSerialCode = ""
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        serialsAdapter.notifyDataSetChanged()
        binding.serializedScanLayout.scannedQty.text = "0"
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.countedQty.editText?.setText("")
        binding.serializedScanLayout.serialNo.editText?.setText("")

    }

    override fun OnRemovedSerial(position: Int) {
        val removedSerial =
            selectedMaterial!!.serials.find { it.serial == scannedSerials[position].serial }
        removedSerial?.isReceived = false
        scannedSerials.removeAt(position)
        serialsAdapter.notifyItemRemoved(position)
        binding.serializedScanLayout.scannedQty.text = "${scannedSerials.size}"
    }
}