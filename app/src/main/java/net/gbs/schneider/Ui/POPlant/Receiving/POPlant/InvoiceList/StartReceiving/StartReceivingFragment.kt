package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Model.RejectionReason
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.ScannedSerialsAdapter
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_INFO
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.containsOnlyDigits
import net.gbs.schneider.Tools.Tools.getEditTextText
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListAdapter
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg.SerialsListDialog
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingMenuFragment.Companion.IS_PO_VENDOR_KEY
import net.gbs.schneider.databinding.FragmentStartReceivingBinding

class StartReceivingFragment :
    BaseFragmentWithViewModel<StartReceivingViewModel, FragmentStartReceivingBinding>(),
    MaterialListAdapter.OnMaterialItemClicked, BarcodeListener, TriggerListener,
    ScannedSerialsAdapter.OnSerialButtonsClicked, OnClickListener {

    companion object {
        fun newInstance() = StartReceivingFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartReceivingBinding
        get() = FragmentStartReceivingBinding::inflate
    private lateinit var materialListDialog: MaterialListDialog
    private lateinit var serialsListDialog: SerialsListDialog
    private lateinit var invoice: Invoice
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        materialListDialog = MaterialListDialog(requireContext(), invoice.materials, this)
        binding.let {
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.invoiceNumber.text = invoice.invoiceNumber
            it.projectNumber.text = invoice.projectNumber
            if (invoice.storagePermissions.isNotEmpty()) {
                binding.mrn.text = invoice.storagePermissions[0].mrn
                binding.billOfLadingNo.text = invoice.storagePermissions[0].billOfLadingNo
                binding.declarationNo.text = invoice.storagePermissions[0].declarationNo
            }
        }
    }

    private var scannedQty = 0
    private var bulkSerialCode = ""
    private var isPoVendor:Boolean? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoice = requireArguments().getString(INVOICE_KEY)?.let { Invoice.fromJson(it) }!!
        isPoVendor = requireArguments().getBoolean(IS_PO_VENDOR_KEY)
        fillData()
        viewModel.getRejectionRequestsList()
        setUpRejectionRequestsSpinner()
        observeGettingRejectionRequestsList()
        binding.inspect.visibility = GONE
        attachButtonsToListener(
            this,
            binding.clearSerial,
            binding.save,
            binding.inspect,
            binding.receivedList,
            binding.clearMaterialData,
            binding.materialList,
            binding.serialList
        )
        clearInputLayoutError(
            binding.materialCode,
            binding.boxNumber,
            binding.serialNo,
            binding.countedQty,
            binding.rejectedQty,
            binding.rejectionReason
        )
        setUpScannedSerialRecyclerView()
        watchQtyText()

        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            if (!USER_INFO!!.canSelectMaterial) {
                binding.materialCode.editText?.setText("")
                warningDialog(
                    requireContext(),
                    getString(R.string.this_user_doesn_t_have_the_authority_to_enter_item_code_please_scan_it)
                )
            } else {
                val material = invoice.materials.find { materialCode == it.materialCode }
                if (material != null) {
                    selectedMaterial = material
                    fillMaterialData()
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            }
        }


        EditTextActionHandler.OnEnterKeyPressed(binding.serialNo) {
            val serialNo = binding.serialNo.editText?.text.toString().trim()
            if (!isPoVendor!!) {
                val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
                if (serial != null) {
                    if (selectedMaterial!!.isSerializedWithOneSerial) {
                        binding.serialNo.editText?.isEnabled = false
                        scannedQty =
                            if (getEditTextText(binding.countedQty.editText!!).isEmpty()) {
                                1
                            } else {
                                getEditTextText(binding.countedQty.editText!!).toInt()+1
                            }
                        if (scannedQty > selectedMaterial!!.remainingQty) {
                            binding.countedQty.error =
                                getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQty
                        } else {
                            binding.countedQty.editText?.setText(scannedQty.toString())
                        }

                    } else {
                        val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                        if (scannedSerial == null) {
                            if (!serial.isReceived) {
                                if (!serial.isRejected) {
                                    binding.serialNo.editText?.setText(serialNo)
                                    serial.isReceived = true
                                    serial.isRejected = false
                                    scannedSerialsList.add(serial)
                                    scannedSerialsAdapter.notifyDataSetChanged()
                                    binding.scannedQty.text =
                                        "${scannedSerialsList.size}"
                                    binding.serialNo.editText?.setText("")
                                    serialsListDialog.setSerialAsScanned(serial)
                                } else {
                                    warningDialog(
                                        requireContext(),
                                        getString(R.string.serial_already_rejected)
                                    )
                                }
                            } else {
                                warningDialog(
                                    requireContext(),
                                    getString(R.string.serial_received_before)
                                )
                            }
                        } else {
                            warningDialog(
                                requireContext(), getString(R.string.serial_added_before)
                            )
                        }
                    }
                } else {
                    warningDialog(
                        requireContext(), getString(R.string.wrong_serial_no)
                    )
                }
            }else{
                if (selectedMaterial!!.isSerializedWithOneSerial) {
                    val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
                    if (serial != null) {
                        binding.serialNo.editText?.isEnabled = false
                        scannedQty =
                            if (getEditTextText(binding.countedQty.editText!!).isEmpty()) {
                                1
                            } else {
                                getEditTextText(binding.countedQty.editText!!).toInt()+1
                            }
                        if (scannedQty > selectedMaterial!!.remainingQty) {
                            binding.countedQty.error =
                                getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQty
                        } else {
                            binding.countedQty.editText?.setText(scannedQty.toString())
                        }
                    } else {
                        warningDialog(
                            requireContext(), getString(R.string.wrong_serial_no)
                        )
                    }
                } else {
                    val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                    if (scannedSerial == null) {
                        if (scannedSerialsList.size<selectedMaterial?.remainingQty!!) {
                            val serial = Serial(serial = serialNo, isReceived = true, isRejected = false)
                            binding.serialNo.editText?.setText(serialNo)
                            scannedSerialsList.add(serial)
                            scannedSerialsAdapter.notifyDataSetChanged()
                            binding.scannedQty.text =
                                "${scannedSerialsList.size}"
                            binding.serialNo.editText?.setText("")
                            serialsListDialog.setSerialAsScanned(serial)
                        } else {
                            warningDialog(
                                requireContext(), getString(R.string.all_quantity_is_already_scanned)
                            )
                        }
                    } else {
                        warningDialog(
                            requireContext(), getString(R.string.serial_added_before)
                        )
                    }
                }
            }
        }
        observeReceiving()
        watchMaterialCodeText()
    }

    private fun watchQtyText() {
        binding.rejectedQty.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val rejectedQtyText = binding.rejectedQty.editText?.text.toString().trim()
                if (rejectedQtyText.isEmpty()) {
                    clearRejectionReason()
                } else {
                    try {
                        val rejectedQty = rejectedQtyText.toInt()
                        if (rejectedQty == 0) {
                            clearRejectionReason()
                        } else {
                            binding.rejectionReason.visibility = VISIBLE
                        }
                    } catch (ex: Exception) {
                        binding.rejectedQty.error =
                            getString(R.string.please_enter_valid_quantity)
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.serialNo.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val serialCode = getEditTextText(binding.serialNo.editText!!)
                if (serialCode.isEmpty()) {
                    binding.clearSerial.visibility = GONE
                } else {
                    if (selectedMaterial!!.isSerializedWithOneSerial)
                        binding.clearSerial.visibility = VISIBLE
                    else
                        binding.clearSerial.visibility = GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }

    private var rejectionReasonsList: List<RejectionReason> = listOf()
    private lateinit var rejectionReasonsAdapter: ArrayAdapter<RejectionReason>
    private var selectedRejectionReasonId: Int? = null
    private fun setUpRejectionRequestsSpinner() {
        rejectionReasonsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            rejectionReasonsList
        )
        binding.rejectionReasonSpinner.setAdapter(rejectionReasonsAdapter)
        binding.rejectionReasonSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedRejectionReasonId = rejectionReasonsList[position].rejectionReasonId
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
        binding.materialCode.editText?.doOnTextChanged { text, _, _, _ ->
            if (text?.isEmpty()!!) {
                binding.clearMaterialData.visibility = INVISIBLE
                binding.materialList.visibility = VISIBLE
            } else {
                binding.clearMaterialData.visibility = VISIBLE
                binding.materialList.visibility = INVISIBLE
            }
        }
    }

    private fun observeReceiving() {
        viewModel.resultInvoiceLiveData.observe(requireActivity()) {
            if (it.isSuccessful) {
                if (it.body()?.responseStatus?.isSuccess!!) {
                    invoice = it.body()?.invoice!!
                    if (invoice.materials.isEmpty())
                        back(this)
                    fillData()
                    clearMaterialData()
                    showSuccessAlerter(
                        it.body()?.responseStatus?.statusMessage!!,
                        requireActivity()
                    )
                } else
                    warningDialog(
                        requireActivity(),
                        it.body()?.responseStatus?.statusMessage!!
                    )
            } else warningDialog(
                requireContext(),
                getString(R.string.error_in_getting_data)
            )
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.serialNo.editText?.setText("")
        binding.boxNumber.editText?.setText("")
        binding.rejectionReasonSpinner.setText("", false)
        binding.rejectionReason.visibility = GONE
        scannedSerialsList.forEach { scannedSerial ->
            selectedMaterial?.serials?.forEach{ serial ->
                if (serial.serial==scannedSerial.serial){
                    serial.isReceived = false
                    serial.isRejected = false
                }
            }
        }
        scannedSerialsList.clear()
        scannedSerialsAdapter.notifyDataSetChanged()
        binding.serialNo.editText?.isEnabled = true
        scannedQty = 0
        selectedMaterial = null
        bulkSerialCode = ""

    }

    private lateinit var scannedSerialsAdapter: ScannedSerialsAdapter
    private val scannedSerialsList = mutableListOf<Serial>()
    private fun setUpScannedSerialRecyclerView() {
        scannedSerialsAdapter = ScannedSerialsAdapter(this, scannedSerialsList)
        binding.scannedSerials.adapter = scannedSerialsAdapter
    }

    private var selectedMaterial: Material? = null

    override fun onMaterialItemClicked(material: Material) {
        if (isClickableMaterial) {
            if (USER_INFO!!.canSelectMaterial) {
                selectedMaterial = material
                fillMaterialData()
                materialListDialog.dismiss()
            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.this_user_doesn_t_have_the_authority_to_select_item_code_please_scan_it)
                )
            }
        }
    }
    private var isMax = false
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (selectedMaterial == null) {
                val material = invoice.materials.find { it.materialCode == scannedText }
                if (material != null) {
                    selectedMaterial = material
                    fillMaterialData()
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            } else {
                val serialNo = scannedText
                if (!isPoVendor!!) {
                    val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
                    if (serial != null) {
                        if (selectedMaterial!!.isSerializedWithOneSerial) {
                            binding.serialNo.editText?.setText(serialNo)
                            binding.serialNo.editText?.isEnabled = false
                            scannedQty =
                                if (getEditTextText(binding.countedQty.editText!!).isEmpty()) {
                                    1
                                } else {
                                    getEditTextText(binding.countedQty.editText!!).toInt()+1
                                }
                            if (scannedQty > selectedMaterial!!.remainingQty) {
                                binding.countedQty.error =
                                    getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQty
                            } else {
                                binding.countedQty.editText?.setText(scannedQty.toString())
                            }

                        } else {
                            val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                            if (scannedSerial == null) {
                                if (!serial.isReceived) {
                                    if (!serial.isRejected) {
                                        binding.serialNo.editText?.setText(serialNo)
                                        serial.isReceived = true
                                        serial.isRejected = false
                                        scannedSerialsList.add(serial)
                                        scannedSerialsAdapter.notifyDataSetChanged()
                                        binding.scannedQty.text =
                                            "${scannedSerialsList.size}"
                                        binding.serialNo.editText?.setText("")
                                        serialsListDialog.setSerialAsScanned(serial)
                                    } else {
                                        warningDialog(
                                            requireContext(),
                                            getString(R.string.serial_already_rejected)
                                        )
                                    }
                                } else {
                                    warningDialog(
                                        requireContext(),
                                        getString(R.string.serial_received_before)
                                    )
                                }
                            } else {
                                warningDialog(
                                    requireContext(), getString(R.string.serial_added_before)
                                )
                            }
                        }
                    } else {
                        warningDialog(
                            requireContext(), getString(R.string.wrong_serial_no)
                        )
                    }
                }else{
                    if (selectedMaterial!!.isSerializedWithOneSerial) {
                        val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
                        if (serial != null) {
                            binding.serialNo.editText?.setText(serialNo)
                            binding.serialNo.editText?.isEnabled = false
                            scannedQty =
                                if (getEditTextText(binding.countedQty.editText!!).isEmpty()) {
                                    1
                                } else {
                                    getEditTextText(binding.countedQty.editText!!).toInt()+1
                                }
                            if (scannedQty > selectedMaterial!!.remainingQty) {
                                binding.countedQty.error =
                                    getString(R.string.max_quantity_is) + selectedMaterial!!.remainingQty
                            } else {
                                binding.countedQty.editText?.setText(scannedQty.toString())
                            }
                        } else {
                            warningDialog(
                                requireContext(), getString(R.string.wrong_serial_no)
                            )
                        }
                    } else {
                        val scannedSerial = scannedSerialsList.find { it.serial == serialNo }
                        if (scannedSerial == null) {
                            if (scannedSerialsList.size<selectedMaterial?.remainingQty!!) {
                                val serial = Serial(serial = serialNo, isReceived = true, isRejected = false)
                                binding.serialNo.editText?.setText(serialNo)
                                scannedSerialsList.add(serial)
                                scannedSerialsAdapter.notifyDataSetChanged()
                                binding.scannedQty.text =
                                    "${scannedSerialsList.size}"
                                binding.serialNo.editText?.setText("")
                                serialsListDialog.setSerialAsScanned(serial)
                            } else {
                                warningDialog(
                                    requireContext(), getString(R.string.all_quantity_is_already_scanned)
                                )
                            }
                        } else {
                            warningDialog(
                                requireContext(), getString(R.string.serial_added_before)
                            )
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
            it.boxNumber.editText?.setText(selectedMaterial!!.boxNumber)
            it.receivedQtyPerShippedQty.text =
                "${selectedMaterial!!.receivedQty} / ${selectedMaterial!!.shippedQuantity}"
            it.scannedQty.text = "0"
            it.countedQty.editText?.setText("")
            it.rejectedQty.editText?.setText("")
            it.materialDataGroup.visibility = VISIBLE
            it.serialNo.error = null
            if (selectedMaterial!!.isSerializedWithOneSerial) {
                it.serialNo.visibility = VISIBLE
                it.scannedQty.visibility = GONE
                it.scannedSerials.visibility = GONE
                it.countedQty.visibility = VISIBLE
                it.rejectedQty.visibility = VISIBLE
                it.serialList.visibility = VISIBLE
                it.clearSerial.visibility = VISIBLE
                binding.countedQty.isEnabled = USER_INFO!!.canEnterQty
                serialsListDialog = SerialsListDialog(requireContext(), selectedMaterial!!)
                binding.countedQty.editText?.setText("")
            } else {
                it.clearSerial.visibility = GONE
                if (selectedMaterial!!.isSerialized) {
                    it.serialNo.visibility = VISIBLE
                    it.scannedQty.visibility = VISIBLE
                    it.scannedSerials.visibility = VISIBLE
                    it.countedQty.visibility = GONE
                    it.rejectedQty.visibility = GONE
                    it.serialList.visibility = VISIBLE
                    serialsListDialog = SerialsListDialog(requireContext(), selectedMaterial!!)

                } else {
                    it.serialNo.visibility = GONE
                    it.scannedQty.visibility = GONE
                    it.scannedSerials.visibility = GONE
                    it.countedQty.visibility = VISIBLE
                    it.rejectedQty.visibility = VISIBLE
                    it.serialList.visibility = GONE
                    binding.countedQty.editText?.setText("")
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
        changeTitle(getString(R.string.start_receiving), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onSerialRejected(position: Int, isRejected: Boolean) {
        scannedSerialsList[position].isRejected = isRejected
        serialsListDialog.setSerialAsRejected(scannedSerialsList[position])
        val serial = scannedSerialsList.find { it.isRejected }
        if (serial != null) {
            binding.rejectionReason.visibility = VISIBLE
        } else {
            clearRejectionReason()
        }
    }

    private fun clearRejectionReason() {
        selectedRejectionReasonId = null
        binding.rejectionReasonSpinner.setText("", false)
        binding.rejectionReason.visibility = GONE
    }

    override fun onRemoveButtonClicked(position: Int) {
        scannedSerialsList[position].isReceived = false
        scannedSerialsList[position].isRejected = false
        serialsListDialog.setSerialAsScanned(scannedSerialsList[position])
//        serialsListDialog.setSerialAsNotRejected(scannedSerialsList[position])
        scannedSerialsList.removeAt(position)
        scannedSerialsAdapter.notifyDataSetChanged()
        binding.scannedQty.text = "${scannedSerialsList.size}"
        if (scannedSerialsList.find { it.isRejected } == null) {
            binding.rejectionReason.visibility = GONE
        } else {
            binding.rejectionReason.visibility = VISIBLE
        }

    }

    private var isClickableMaterial = false
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if (selectedMaterial != null) {
                    if ((binding.rejectionReason.visibility == VISIBLE && selectedRejectionReasonId != null)
                        || (binding.rejectionReason.visibility == GONE && selectedRejectionReasonId == null)
                    ) {
                        if (selectedMaterial!!.isSerializedWithOneSerial) {
                            val serialNo = binding.serialNo.editText?.text.toString().trim()
                            val bulkQtyText = binding.countedQty.editText?.text.toString().trim()

                            if (serialNo.isNotEmpty()) {
                                var bulkQty = 0
                                if (bulkQtyText.isNotEmpty()) {
                                    try {
                                        bulkQty = bulkQtyText.toInt()
                                    } catch (ex: Exception) {
                                        binding.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                    if (bulkQty <= selectedMaterial!!.remainingQty) {
                                        val rejectedQtyText =
                                            binding.rejectedQty.editText?.text.toString().trim()
                                        var rejectedQty = 0
                                        val serial = Serial(
                                            serial = binding.serialNo.editText?.text.toString()
                                                .trim(),
                                            isReceived = true
                                        )
                                        scannedSerialsList.add(serial)
                                        if (rejectedQtyText.isNotEmpty()) {
                                            try {
                                                rejectedQty = rejectedQtyText.toInt()
                                                if (rejectedQty <=
                                                    selectedMaterial!!.shippedQuantity
                                                    - selectedMaterial!!.acceptedQty
                                                    - selectedMaterial!!.rejectedQty
                                                    - bulkQty
                                                ) {
                                                    viewModel.ReceiveInvoice_Serialized(
                                                        bulkQty = bulkQty,
                                                        isBulk = true,
                                                        rejectionReasonId = selectedRejectionReasonId,
                                                        invoice = invoice,
                                                        selectedMaterial = selectedMaterial!!,
                                                        scannedSerialsList = scannedSerialsList,
                                                        loadingDialog = loadingDialog,
                                                        rejectedQty = rejectedQty
                                                    )

                                                } else {
                                                    binding.rejectedQty.error =
                                                        getString(R.string.please_enter_valid_quantity)
                                                }
                                            } catch (ex: Exception) {
                                                binding.rejectedQty.error =
                                                    getString(R.string.please_enter_valid_quantity)
                                            }
                                        } else {

                                            viewModel.ReceiveInvoice_Serialized(
                                                bulkQty = bulkQty,
                                                isBulk = true,
                                                rejectionReasonId = selectedRejectionReasonId,
                                                invoice = invoice,
                                                selectedMaterial = selectedMaterial!!,
                                                scannedSerialsList = scannedSerialsList,
                                                loadingDialog = loadingDialog,
                                                rejectedQty = 0
                                            )
                                        }
                                        Log.d(TAG, "====================onClick: ${scannedSerialsList.size}")
                                    } else {
                                        binding.countedQty.error =
                                            getString(R.string.please_enter_valid_quantity)
                                    }
                                } else binding.countedQty.error =
                                    getString(R.string.please_enter_quantity)
                            } else {
                                binding.serialNo.error = getString(R.string.please_scan_serial)
                            }
                        } else {
                            if (selectedMaterial!!.isSerialized) {
                                if (scannedSerialsList.isNotEmpty()) {
                                    viewModel.ReceiveInvoice_Serialized(
                                        0,
                                        false,
                                        selectedRejectionReasonId,
                                        invoice,
                                        selectedMaterial!!,
                                        scannedSerialsList,
                                        loadingDialog
                                    )
                                } else {
                                    warningDialog(
                                        requireContext(),
                                        getString(R.string.please_scan_serials)
                                    )
                                }
                            } else {
                                val receivedQtyText =
                                    binding.countedQty.editText?.text.toString().trim()
                                val rejectedQtyText =
                                    binding.rejectedQty.editText?.text.toString().trim()
                                if (receivedQtyText.isNotEmpty()) {
                                    if (containsOnlyDigits(receivedQtyText)) {
                                        val receivedQty = receivedQtyText.toInt()
                                        val rejectedQty: Int
                                        if (rejectedQtyText.isNotEmpty()) {
                                            if (containsOnlyDigits(rejectedQtyText)) {
                                                rejectedQty = rejectedQtyText.toInt()
                                                if (rejectedQty <= receivedQty) {
                                                    viewModel.ReceiveInvoice_Unserialized(
                                                        invoice,
                                                        selectedMaterial!!,
                                                        receivedQty,
                                                        rejectedQty,
                                                        rejectionReasonId = selectedRejectionReasonId,
                                                        loadingDialog
                                                    )
                                                } else {
                                                    binding.rejectedQty.error =
                                                        getString(R.string.please_enter_valid_quantity)
                                                }
                                            } else
                                                binding.countedQty.error =
                                                    getString(R.string.please_enter_valid_quantity)
                                        } else {
                                            viewModel.ReceiveInvoice_Unserialized(
                                                invoice,
                                                selectedMaterial!!,
                                                receivedQty,
                                                0,
                                                selectedRejectionReasonId,
                                                loadingDialog
                                            )
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
                        }
                    } else {
                        binding.rejectionReason.error =
                            getString(R.string.please_select_rejection_reason)
                    }
                } else {
                    binding.materialCode.error =
                        getString(R.string.please_scan_or_select_material_first)
                }
            }

            R.id.received_list -> {
                isClickableMaterial = false
                materialListDialog.show()
            }

            R.id.clear_material_data -> {
                clearMaterialData()
            }

            R.id.material_list -> {
                isClickableMaterial = true
                materialListDialog.show()

            }

            R.id.serial_list -> {
                serialsListDialog.show()
            }

            R.id.clear_serial -> {
                if (selectedMaterial!!.isSerializedWithOneSerial) {
                    binding.serialNo.editText?.setText("")
                    binding.countedQty.editText?.setText("")
                    scannedQty = 0
                    bulkSerialCode = ""
                    binding.serialNo.isEnabled = true
                }
            }
        }
    }
}