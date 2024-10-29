package net.gbs.schneider.Ui.Return.PutAway.StartPutAwayReturn

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_UnserializedBody
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.SerialPutAwayReturn
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.PutAwayReturnMaterialListAdapter
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.PutAwayReturnMaterialListDialog
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg.PutAwaySerialsListDialog_Return
import net.gbs.schneider.SerialsAdapters.ScannedSerialsAdapter
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler.OnEnterKeyPressed
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.containsOnlyDigits
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentStartPutAwayReturnBinding

class StartPutAwayReturnFragment :
    BaseFragmentWithViewModel<StartPutAwayReturnViewModel, FragmentStartPutAwayReturnBinding>(),
    PutAwayReturnMaterialListAdapter.OnMaterialItemClicked,
    com.honeywell.aidc.BarcodeReader.BarcodeListener,
    com.honeywell.aidc.BarcodeReader.TriggerListener, ScannedSerialsAdapter.OnSerialButtonsClicked,
    View.OnClickListener, SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = StartPutAwayReturnFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartPutAwayReturnBinding
        get() = FragmentStartPutAwayReturnBinding::inflate
    private lateinit var materialListDialog: PutAwayReturnMaterialListDialog
    private lateinit var serialsListDialog: PutAwaySerialsListDialog_Return
    private lateinit var workOrderReturn: WorkOrderReturn
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        materialListDialog =
            PutAwayReturnMaterialListDialog(requireContext(), workOrderReturn.materials, this)
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
            ?.let { WorkOrderReturn.fromJson(it) }!!
        fillData()
        observeBinCodeData()
        observePutAwayResult()

        Tools.clearInputLayoutError(
            binding.serializedScanLayout.serialNo,
            binding.materialCode,
            binding.binCode,
            binding.unserializedEnterQtyLayout.countedQty,
            binding.oneSerialItemScanLayout.countedQty,
            binding.oneSerialItemScanLayout.serialNo
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
            binding.oneSerialItemScanLayout.countedQty.error = null
            binding.oneSerialItemScanLayout.serialNo.error = null
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
        OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            val material = workOrderReturn.materials.find { it.materialCode == materialCode }
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

        OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
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
        OnEnterKeyPressed(binding.serializedScanLayout.serialNo){
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
                    scannedSerialsList.add(serial)
                    binding.serializedScanLayout.serialNo.editText?.setText("")
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
            clearMaterialData()
            if (it.materials.isEmpty())
                back(this)
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.serializedScanLayout.root.visibility = GONE
        binding.oneSerialItemScanLayout.root.visibility = GONE
        binding.unserializedEnterQtyLayout.root.visibility = GONE
        binding.materialCode.editText?.setText("")
        selectedMaterial = null
        scannedQty = 0
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        scannedSerialsList.clear()
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        rejectedSerialsAdapter.notifyDataSetChanged()
        binding.serializedScanLayout.serialNo.editText?.setText("")
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
        binding.serializedScanLayout.scannedSerials.adapter = rejectedSerialsAdapter

    }

    private var selectedMaterial: MaterialReturn? = null

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
                    PutAwaySerialsListDialog_Return(requireContext(), selectedMaterial!!)
            } else {
                if (selectedMaterial!!.isSerialized) {
                    binding.serializedScanLayout.scannedQty.text =
                        "${scannedSerialsList.size}"
                    showSerializedLayout()
                    serialsListDialog =
                        PutAwaySerialsListDialog_Return(requireContext(), selectedMaterial!!)
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
                                        if (qty <= selectedMaterial!!.remainingPutAwayQty) {
                                            val body = PutAwayReturn_SerializedBody(
                                                serials = listOf(
                                                    SerialPutAwayReturn(
                                                        serial = serialNo,
                                                        isPutaway = true
                                                    )
                                                ),
                                                StorageBinCode = binCode,
                                                WorkOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                                WorkOrderReturnDetailsId = selectedMaterial!!.workOrderReturnDetailsId!!,
                                                IsBulk = true,
                                                BulkQty = qty
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
                                    val putawayinvoiceSerializedbody = PutAwayReturn_SerializedBody(
                                        WorkOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                        WorkOrderReturnDetailsId = selectedMaterial?.workOrderReturnDetailsId!!,
                                        serials = serials,
                                        StorageBinCode = binCode,
                                        IsBulk = false,
                                        BulkQty = scannedQty,
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
                                    if (containsOnlyDigits(putAwayQtyText)) {
                                        val putAwayQty = putAwayQtyText.toInt()
                                        if (putAwayQty <= selectedMaterial!!.remainingPutAwayQty) {
                                            val body = PutAwayReturn_UnserializedBody(
                                                WorkOrderReturnId = workOrderReturn.workOrderReturnId!!,
                                                WorkOrderReturnDetailsId = selectedMaterial!!.workOrderReturnDetailsId!!,
                                                StorageBinCode = binCode,
                                                PutAwayQty = putAwayQty
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

    override fun onMaterialItemClicked(material: MaterialReturn) {
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