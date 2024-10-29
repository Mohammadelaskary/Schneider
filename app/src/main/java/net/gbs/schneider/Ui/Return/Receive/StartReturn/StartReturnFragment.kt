package net.gbs.schneider.Ui.Return.Receive.StartReturn

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_UnserializedBody
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.Model.ReturnReason
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.ReturnMaterialListAdapter
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.ReturnMaterialListDialog
import net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg.SerialsListDialog_Return
import net.gbs.schneider.SerialsAdapters.ScannedSerialsAdapter
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.containsOnlyDigits
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentStartReturnBinding

class StartReturnFragment :
    BaseFragmentWithViewModel<StartReturnViewModel, FragmentStartReturnBinding>(),
    ReturnMaterialListAdapter.OnMaterialItemClicked,
    com.honeywell.aidc.BarcodeReader.BarcodeListener,
    com.honeywell.aidc.BarcodeReader.TriggerListener, ScannedSerialsAdapter.OnSerialButtonsClicked,
    View.OnClickListener, SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = StartReturnFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartReturnBinding
        get() = FragmentStartReturnBinding::inflate
    private lateinit var materialListDialog: ReturnMaterialListDialog
    private lateinit var serialsListDialog: SerialsListDialog_Return
    private lateinit var returnWorkOrder: WorkOrderReturn
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        materialListDialog =
            ReturnMaterialListDialog(requireContext(), returnWorkOrder.materials, this)
        binding.let {
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.workOrderNumber.text = returnWorkOrder.workOrderNumber
            it.projectNumber.text = returnWorkOrder.projectNumber
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        returnWorkOrder = requireArguments().getString(IntentKeys.WORK_ORDER_KEY)
            ?.let { WorkOrderReturn.fromJson(it) }!!
        fillData()
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        Tools.clearInputLayoutError(
            binding.serializedScanLayout.serialNo,
            binding.materialCode,
            binding.returnReason,
            binding.unserializedEnterQtyLayout.countedQty,
            binding.oneSerialItemScanLayout.serialNo,
            binding.oneSerialItemScanLayout.countedQty
        )
        setUpScannedSerialRecyclerView()
        viewModel.getReturnReasonList()
        observeReturnReason()
        setUpReturnReasonListSpinnerAdapter()

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
            for (material in returnWorkOrder.materials) {
                if (materialCode == material.materialCode) {
                    selectedMaterial = material
                    fillMaterialData()
                    break
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.serializedScanLayout.serialNo) {
            val serialNo = binding.serializedScanLayout.serialNo.editText?.text.toString()
            val serial = selectedMaterial?.returnedSerials?.find { it.serial == serialNo }
            if (serial != null) {
                val scannedSerial = returnedSerialsList.find { it.serial == serialNo }
                if (scannedSerial == null) {
                    val serial = Serial(
                        isReceived = true, serial = serialNo,
                    )
                    binding.serializedScanLayout.serialNo.editText?.setText("")
                    returnedSerialsList.add(serial)
                    returnedSerialsAdapter.notifyDataSetChanged()
                    binding.serializedScanLayout.scannedQty.text =
                        "${returnedSerialsList.size} / ${selectedMaterial!!.returnedQuantity}"
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
        EditTextActionHandler.OnEnterKeyPressed(binding.oneSerialItemScanLayout.serialNo) {
            val serialNo = binding.oneSerialItemScanLayout.serialNo.editText?.text.toString()
            val serial = selectedMaterial?.issueWorkOrderSerials?.find { it.serial == serialNo }
            if (serial != null) {
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
            binding.oneSerialItemScanLayout.serialNo.isEnabled = true
        }
        observeReturning()
    }

    private fun observeReturnReason() {
        viewModel.getReturnReasonListStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getReturnReasonListLiveData.observe(requireActivity()) {
            returnReasonsList = it
            returnReasonsListAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                returnReasonsList
            )
            binding.returnReasonSpinner.setAdapter(returnReasonsListAdapter)
        }
    }

    lateinit var returnReasonsListAdapter: ArrayAdapter<ReturnReason>
    var selectedReturnReason: ReturnReason? = null
    var returnReasonsList: List<ReturnReason> = listOf()
    private fun setUpReturnReasonListSpinnerAdapter() {
        returnReasonsListAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, returnReasonsList)
        binding.returnReasonSpinner.setAdapter(returnReasonsListAdapter)
        binding.returnReasonSpinner.setOnItemClickListener { adapterView, view, i, l ->
            selectedReturnReason = returnReasonsList[i]
        }
    }

    private fun observeReturning() {
        viewModel.returnResponseStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.returnResponse.observe(requireActivity()) {
            returnWorkOrder = it
            if (returnWorkOrder.materials.isEmpty())
                back(this)
            fillData()
            clearMaterialData()
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
        binding.serializedScanLayout.serialNo.editText?.setText("")
        returnedSerialsList.clear()
        binding.oneSerialItemScanLayout.serialNo.editText?.isEnabled = true
        binding.oneSerialItemScanLayout.serialNo.editText?.setText("")
        binding.oneSerialItemScanLayout.countedQty.editText?.setText("")
        returnedSerialsAdapter.notifyDataSetChanged()
    }


    private lateinit var returnedSerialsAdapter: SerialsWithRemoveButtonAdapter
    private val returnedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        returnedSerialsAdapter = SerialsWithRemoveButtonAdapter(returnedSerialsList, this)
        binding.serializedScanLayout.scannedSerials.adapter = returnedSerialsAdapter
    }

    private var selectedMaterial: MaterialReturn? = null

    override fun onMaterialItemClicked(material: MaterialReturn) {
        selectedMaterial = material
        fillMaterialData()
        materialListDialog.dismiss()
    }

    private var scannedQty = 0
    private var isMax = false
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            if (binding.serializedScanLayout.root.visibility == GONE
                && binding.oneSerialItemScanLayout.root.visibility == GONE
                && binding.unserializedEnterQtyLayout.root.visibility == GONE
            ) {
                val materialCode = barcodeReader.scannedData(p0!!)
                for (material in returnWorkOrder.materials) {
                    if (materialCode == material.materialCode) {
                        selectedMaterial = material
                        fillMaterialData()
                        break
                    } else {
                        binding.materialCode.error = getString(R.string.wrong_material_code)
                    }
                }
            } else {
                if (selectedMaterial!!.isSerializedWithOneSerial) {
                    val serialNo = barcodeReader.scannedData(p0!!)
                    val serial =
                        selectedMaterial?.issueWorkOrderSerials?.find { it.serial == serialNo }
                    if (serial != null) {
                        if (getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!).isEmpty()){
                            binding.oneSerialItemScanLayout.serialNo.editText?.setText(serialNo)
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
                } else {
                    val serialNo = barcodeReader.scannedData(p0!!)
//                    binding.serializedScanLayout?.serialNo?.editText?.setText(serialNo)
                    val serial =
                        selectedMaterial?.issueWorkOrderSerials?.find { it.serial == serialNo }
                    if (serial != null) {
                        if (returnedSerialsList.size < selectedMaterial?.remainingQty!!) {
                            val scannedSerial = returnedSerialsList.find { it.serial == serialNo }
                            if (scannedSerial == null) {
                                val serial = Serial(
                                    isReceived = true, serial = serialNo,
                                )
                                returnedSerialsList.add(serial)
                                returnedSerialsAdapter.notifyDataSetChanged()
                                binding.serializedScanLayout.scannedQty.text =
                                    "${returnedSerialsList.size}"
                            } else {
                                warningDialog(
                                            requireContext(),
                                    getString(R.string.serial_added_before)
                                )
                            }
                        } else {
                            warningDialog(
                                            requireContext(),
                                getString(R.string.all_serials_are_scanned))
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

    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial?.materialCode)
            it.materialDesc.text = selectedMaterial?.materialName
            it.returnQtyText.text =
                selectedMaterial?.receivedQuantity.toString() + "/" + selectedMaterial?.returnedQuantity.toString()
            it.materialDataGroup.visibility = VISIBLE
            it.oneSerialItemScanLayout.serialNo.editText?.setText("")
            it.oneSerialItemScanLayout.countedQty.editText?.setText("")
            if (selectedMaterial?.isSerializedWithOneSerial!!) {
                showOneSerialLayout()
                it.serializedScanLayout.scannedQty.text = scannedQty.toString()
                serialsListDialog =
                    SerialsListDialog_Return(requireContext(), selectedMaterial!!)
            } else {
                if (selectedMaterial?.isSerialized == true) {
                    showSerializedLayout()
                    it.serializedScanLayout.scannedQty.text = scannedQty.toString()
                    serialsListDialog =
                        SerialsListDialog_Return(requireContext(), selectedMaterial!!)
                } else {
                    it.serializedScanLayout.serialNo.visibility = GONE
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
        Tools.changeTitle(getString(R.string.start_return), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onSerialRejected(position: Int, isRejected: Boolean) {
        returnedSerialsList[position].isRejected = isRejected
    }

    override fun onRemoveButtonClicked(position: Int) {
        returnedSerialsList.removeAt(position)
        binding.serializedScanLayout.scannedQty.text =
            "${returnedSerialsList.size} / ${selectedMaterial!!.returnedQuantity}"
        returnedSerialsAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if (selectedMaterial != null) {
                    if (selectedReturnReason != null) {
                        if (selectedMaterial?.isSerializedWithOneSerial!!) {
                            val serialNo =
                                getEditTextText(binding.oneSerialItemScanLayout.serialNo.editText!!)
                            val qtyText =
                                binding.oneSerialItemScanLayout.countedQty.editText?.text.toString()
                                    .trim()
                            if (serialNo.isNotEmpty()) {
                                if (qtyText.isNotEmpty()) {
                                    var qty = 0
                                    try {
                                        qty = qtyText.toInt()
                                    } catch (ex: Exception) {
                                        binding.oneSerialItemScanLayout.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                    val serializedBody = ReceiveReturn_SerializedBody(
                                        WorkOrderReturnDetailsId = returnWorkOrder.workOrderReturnId!!,
                                        WorkOrderReturnId = selectedMaterial?.workOrderReturnDetailsId!!,
                                        serials = listOf(
                                            Serial(
                                                serial = serialNo,
                                                isReceived = true,
                                            )
                                        ),
                                        reasonId = selectedReturnReason!!.returnReasonId,
                                        BulkQty = qty,
                                        IsBulk = true,
                                    )
                                    viewModel.receiveReturnSerialized(serializedBody)
                                } else {
                                    binding.oneSerialItemScanLayout.countedQty.error =
                                        getString(R.string.please_enter_qty)
                                }
                            } else {
                                binding.oneSerialItemScanLayout.serialNo.error =
                                    getString(R.string.please_scan_serial)
                            }
                        } else {
                            if (selectedMaterial?.isSerialized!!) {
                                if (returnedSerialsList.isNotEmpty()) {
                                    val serializedBody = ReceiveReturn_SerializedBody(
                                        WorkOrderReturnDetailsId = returnWorkOrder.workOrderReturnId!!,
                                        WorkOrderReturnId = selectedMaterial?.workOrderReturnDetailsId!!,
                                        serials = returnedSerialsList,
                                        reasonId = selectedReturnReason!!.returnReasonId,
                                        BulkQty = 0,
                                        IsBulk = false,
                                    )
                                    viewModel.receiveReturnSerialized(serializedBody)
                                } else {
                                    warningDialog(
                                            requireContext(),
                                        getString(R.string.please_scan_serials))
                                }
                            } else {
                                val returnQtyText =
                                    binding.unserializedEnterQtyLayout.countedQty.editText?.text.toString()
                                        .trim()
                                if (returnQtyText.isNotEmpty()) {
                                    if (containsOnlyDigits(returnQtyText)) {
                                        val returnQty = returnQtyText.toInt()
                                        if (returnQty <= selectedMaterial?.remainingQty!!) {
                                            val unserializedBody = ReceiveReturn_UnserializedBody(
                                                WorkOrderReturnId = returnWorkOrder.workOrderReturnId!!,
                                                WorkOrderReturnDetailsId = selectedMaterial?.workOrderReturnDetailsId!!,
                                                ReceivedQty = returnQty,
                                                ReturnReasonId = selectedReturnReason!!.returnReasonId
                                            )
                                            viewModel.receiveReturnUnserialized(unserializedBody)
                                        } else {
                                            binding.unserializedEnterQtyLayout.countedQty.error =
                                                getString(R.string.return_quantity_must_be_less_or_equal_to) + " ${selectedMaterial?.remainingQty}"
                                        }
                                    } else {
                                        binding.unserializedEnterQtyLayout.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                } else {
                                    binding.unserializedEnterQtyLayout.countedQty.error =
                                        getString(R.string.please_enter_return_quantity)
                                }
                            }
                        }
                    } else {
                        binding.returnReason.error =
                            getString(R.string.please_select_return_reason)
                    }
                } else {
                    binding.materialCode.error =
                        getString(R.string.please_scan_or_select_material_first)
                }
            }
        }
    }

    override fun OnRemovedSerial(position: Int) {
        returnedSerialsList.removeAt(position)
        binding.serializedScanLayout.scannedQty.text =
            "${returnedSerialsList.size}"
        returnedSerialsAdapter.notifyDataSetChanged()
    }
}