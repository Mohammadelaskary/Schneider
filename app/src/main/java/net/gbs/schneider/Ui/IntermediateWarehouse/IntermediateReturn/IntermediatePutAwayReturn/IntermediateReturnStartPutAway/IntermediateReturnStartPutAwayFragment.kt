package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway

import Intermediate.IntermediatePutAwayReturnMaterialListAdapter
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
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_UnserializedBody
import net.gbs.schneider.Model.IntermediateMaterialReturn
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.SerialPutAwayReturn
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.ScannedSerialsAdapter
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway.IntermediatePutAwayReturnMaterialListDialog.IntermediatePutAwayReturnMaterialListDialog
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway.IntermediatePutAwaySerialsListDialog_Return.IntermediatePutAwaySerialsListDialog_Return
import net.gbs.schneider.databinding.FragmentIntermediateReturnStartPutAwayBinding

class IntermediateReturnStartPutAwayFragment :
    BaseFragmentWithViewModel<IntermediateReturnStartPutAwayViewModel, FragmentIntermediateReturnStartPutAwayBinding>(),
    BarcodeListener, TriggerListener,
    IntermediatePutAwayReturnMaterialListAdapter.OnMaterialItemClicked,
    ScannedSerialsAdapter.OnSerialButtonsClicked,
    View.OnClickListener, SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = IntermediateReturnStartPutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateReturnStartPutAwayBinding
        get() = FragmentIntermediateReturnStartPutAwayBinding::inflate

    private lateinit var materialListDialog: IntermediatePutAwayReturnMaterialListDialog
    private lateinit var serialsListDialog: IntermediatePutAwaySerialsListDialog_Return
    private lateinit var workOrderReturn: IntermediateWorkOrderReturn
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        materialListDialog = IntermediatePutAwayReturnMaterialListDialog(
            requireContext(),
            workOrderReturn.materials,
            this
        )
        binding.let {
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.workOrderNumber.text = workOrderReturn.workOrderNumber
            it.projectNumber.text = workOrderReturn.projectNumber
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workOrderReturn = requireArguments().getString(IntentKeys.WORK_ORDER_KEY)
            ?.let { IntermediateWorkOrderReturn.fromJson(it) }!!
        fillData()
        observeBinCodeData()
        observePutAwayResult()

        Tools.clearInputLayoutError(
            binding.serializedScanLayout.serialNo,
            binding.materialCode,
            binding.binCode,
            binding.unserializedEnterQtyLayout.countedQty,
            binding.oneSerialItemScanLayout.serialNo,
            binding.oneSerialItemScanLayout.countedQty
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

        binding.oneSerialItemScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }
        binding.serializedScanLayout.serialList.setOnClickListener {
            serialsListDialog.show()
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            val material = workOrderReturn.materials.find { it.materialCode == materialCode }
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

        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
            val serial = selectedMaterial?.returnedSerials?.find { it.serial == serialNo }
            if (serial != null) {
                binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = false
                scannedQty = if (getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).isEmpty()){
                    1
                } else {
                    getEditTextText(binding.oneSerialItemScanLayout.countedQty.editText!!).toInt()
                }
                binding.oneSerialItemScanLayout.countedQty.editText?.setText(scannedQty.toString())
                if (scannedQty > selectedMaterial!!.remainingPutAwayQty){
                    binding.oneSerialItemScanLayout.countedQty.error =
                        getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingPutAwayQty
                } else if (scannedQty == selectedMaterial!!.remainingPutAwayQty) {
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

        EditTextActionHandler.OnEnterKeyPressed(binding.serializedScanLayout.serialNo) {
            val serialNo = getEditTextText(binding.serializedScanLayout.serialNo.editText!!)
            val serial =
                selectedMaterial!!.returnedSerials.find { it.serial == serialNo }
            if (serial != null) {
                val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                if (scannedSerial == null) {
                    val serial = Serial(
                        serial = serialNo,
                        isPutaway = true
                    )
                    binding.serializedScanLayout.serialNo.editText?.setText("")
                    scannedSerialsList.add(serial)
                    rejectedSerialsAdapter.notifyDataSetChanged()
                    binding.serializedScanLayout.serialNo.error = null
                    binding.serializedScanLayout.scannedQty.text =
                        "${scannedSerialsList.size}"
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
            clearMaterialData()
            if (it.materials.isEmpty())
                Tools.back(this)
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.serializedScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.serializedScanLayout.serialNo.editText?.setText("")
        selectedMaterial = null
        scannedQty = 0
        scannedSerialsList.clear()
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        rejectedSerialsAdapter.notifyDataSetChanged()
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

    private lateinit var rejectedSerialsAdapter: SerialsWithRemoveButtonAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        rejectedSerialsAdapter = SerialsWithRemoveButtonAdapter(scannedSerialsList, this)
        binding.serializedScanLayout.scannedSerials.adapter = rejectedSerialsAdapter

    }

    private var selectedMaterial: IntermediateMaterialReturn? = null
    private var isMax = false

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedCode = barcodeReader.scannedData(p0!!)
            if (binding.binCode.editText?.text!!.isEmpty()) {
                viewModel.getBinData(scannedCode)
            } else {
                if (selectedMaterial == null) {
                    val material = workOrderReturn.materials.find { it.materialCode == scannedCode }
                    if (material != null) {
                        selectedMaterial = material
                        fillMaterialData()
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }
                } else {
                    if (binding.serializedScanLayout.root.visibility == VISIBLE) {
                        val serial =
                            selectedMaterial!!.returnedSerials.find { it.serial == scannedCode }
                        if (serial != null) {
                            val scannedSerial = scannedSerialsList.find { it.serial == scannedCode }
                            if (scannedSerial == null) {
                                val serial = Serial(
                                    serial = scannedCode,
                                    isPutaway = true
                                )
                                scannedSerialsList.add(serial)
                                rejectedSerialsAdapter.notifyDataSetChanged()
                                binding.serializedScanLayout.serialNo.error = null
                                binding.serializedScanLayout.scannedQty.text =
                                    "${scannedSerialsList.size}"
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
                    } else if (binding.oneSerialItemScanLayout.root.visibility == VISIBLE) {
                        val serial =
                            selectedMaterial!!.returnedSerials.find { it.serial == scannedCode }
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
                                if (scannedQty > selectedMaterial!!.remainingPutAwayQty){
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is)+ selectedMaterial!!.remainingPutAwayQty
                                } else if (scannedQty == selectedMaterial!!.remainingPutAwayQty) {
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
                                if (scannedQty > selectedMaterial!!.remainingPutAwayQty) {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.max_quantity_is) + selectedMaterial!!.remainingPutAwayQty
                                } else if (scannedQty < selectedMaterial!!.remainingPutAwayQty) {
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
                    }
                }
            }
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

    private var scannedQty = 0
    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.returnReceivedQtyText.text =
                selectedMaterial!!.putawayQuantity.toString() + "/" + selectedMaterial!!.receivedQuantity.toString()
            it.materialDataGroup.visibility = VISIBLE
            if (selectedMaterial!!.isSerializedWithOneSerial) {
                showOneSerialLayout()
                serialsListDialog =
                    IntermediatePutAwaySerialsListDialog_Return(
                        requireContext(),
                        selectedMaterial!!
                    )
            } else {
                if (selectedMaterial!!.isSerialized) {
                    binding.serializedScanLayout.scannedQty.text =
                        "${scannedSerialsList.size}"
                    showSerializedLayout()
                    serialsListDialog =
                        IntermediatePutAwaySerialsListDialog_Return(
                            requireContext(),
                            selectedMaterial!!
                        )
                } else {
                    showUnSerializedLayout()
                }
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

    override fun onSerialRejected(position: Int, isRejected: Boolean) {
        scannedSerialsList[position].isRejected = isRejected
    }

    override fun onRemoveButtonClicked(position: Int) {
        scannedSerialsList[position].isReceived = false
        scannedSerialsList.removeAt(position)
        rejectedSerialsAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val binCode = binding.binCode.editText?.text.toString().trim()
                if (binCode.isNotEmpty()) {
                    if (selectedMaterial != null) {
                        if (selectedMaterial!!.isSerializedWithOneSerial) {
                            val serialNo =
                                binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
                                    .trim()
                            if (serialNo.isNotEmpty()) {
                                val serial =
                                    selectedMaterial!!.returnedSerials.find { it.serial == serialNo }
                                if (serial != null) {
                                    val qtyText =
                                        binding.oneSerialItemScanLayout.countedQty.editText?.text.toString()
                                            .trim()
                                    if (qtyText.isNotEmpty()) {
                                        var qty = 0
                                        try {
                                            qty = qtyText.toInt()
                                        } catch (ex: Exception) {
                                            binding.oneSerialItemScanLayout.countedQty.error =
                                                getString(R.string.please_enter_valid_quantity)
                                        }
                                        if (qty <= selectedMaterial!!.returnedQuantity!!) {
                                            val body = PutAwayReturnProduction_SerializedBody(
                                                serials = listOf(
                                                    SerialPutAwayReturn(
                                                        serial = serialNo,
                                                        isPutaway = true
                                                    )
                                                ),
                                                storageBinCode = binCode,
                                                workOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                                workOrderReturnDetailsId = selectedMaterial!!.workOrderReturnDetailsId!!,
                                                isBulk = true,
                                                bulkQty = qty
                                            )
                                            viewModel.putAwayInvoice_Serialized(body)
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
                                        getString(R.string.wrong_serial_no)
                                }
                            } else {
                                binding.oneSerialItemScanLayout.serialNo.error =
                                    getString(R.string.please_scan_serial)
                            }
                        } else {
                            if (selectedMaterial!!.isSerialized) {
                                if (scannedSerialsList.isNotEmpty() && selectedMaterial!!.returnedSerials.isNotEmpty()) {
                                    val serials = mutableListOf<SerialPutAwayReturn>()
                                    scannedSerialsList.forEach {
                                        serials.add(
                                            SerialPutAwayReturn(
                                                it.isPutaway, it.serial
                                            )
                                        )
                                    }
                                    val putawayinvoiceSerializedbody =
                                        PutAwayReturnProduction_SerializedBody(
                                            workOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                            workOrderReturnDetailsId = selectedMaterial?.workOrderReturnDetailsId!!,
                                            serials = serials,
                                            storageBinCode = binCode,
                                            isBulk = false,
                                            bulkQty = scannedQty,
                                        )
                                    viewModel.putAwayInvoice_Serialized(putawayinvoiceSerializedbody)
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
                                        if (putAwayQty <= selectedMaterial!!.receivedQuantity!!) {
                                            val body = PutAwayReturnProduction_UnserializedBody(
                                                workOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                                workOrderReturnDetailsId = selectedMaterial!!.workOrderReturnDetailsId!!,
                                                storageBinCode = binCode,
                                                putAwayQty = putAwayQty
                                            )
                                            viewModel.putAwayInvoice_Unserialized(body)
                                        } else {
                                            binding.unserializedEnterQtyLayout.countedQty.error =
                                                getString(R.string.put_away_qty_must_be_less_or_equal_to) + " ${selectedMaterial!!.returnedQuantity}"
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

    override fun onMaterialItemClicked(material: IntermediateMaterialReturn) {
        selectedMaterial = material
        fillMaterialData()
        materialListDialog.dismiss()
    }

    override fun OnRemovedSerial(position: Int) {
        scannedSerialsList.removeAt(position)
        binding.serializedScanLayout.scannedQty.text =
            "${scannedSerialsList.size}"

        rejectedSerialsAdapter.notifyDataSetChanged()
    }


}