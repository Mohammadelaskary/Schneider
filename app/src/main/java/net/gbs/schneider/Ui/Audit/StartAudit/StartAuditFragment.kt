package net.gbs.schneider.Ui.Audit.StartAudit

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
import net.gbs.schneider.Model.AuditOrder
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.MaterialData
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.SerialsAdapters.SerialsWithRemoveButtonAdapter
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartReceivingFragment
import net.gbs.schneider.databinding.FragmentStartAuditBinding

class StartAuditFragment :
    BaseFragmentWithViewModel<StartAuditViewModel, FragmentStartAuditBinding>(),
    com.honeywell.aidc.BarcodeReader.BarcodeListener,
    com.honeywell.aidc.BarcodeReader.TriggerListener,
    View.OnClickListener, SerialsWithRemoveButtonAdapter.OnRemoveSerialButtonClicked {

    companion object {
        fun newInstance() = StartReceivingFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartAuditBinding
        get() = FragmentStartAuditBinding::inflate

    private lateinit var auditOrder: AuditOrder
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    private fun fillData() {
        binding.let {
//            it?.plant?.text = auditOrder.plant
//            it?.warehouseName?.text = auditOrder.warehouseName
            it.plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            it.warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            it.auditNum.text = auditOrder.auditNo
//            it?.auditDate?.text = auditOrder.auditDate.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auditOrder = requireArguments().getString(IntentKeys.AUDIT_ORDER_KEY)
            ?.let { AuditOrder.fromJson(it) }!!
        fillData()
        Tools.clearInputLayoutError(
            binding.serialNo,
            binding.materialCode,
            binding.binCode,
            binding.countedQty
        )
        binding.binCode.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.binCode.error = null
                if (s!!.isEmpty()) {
                    binding.binDataGroup.visibility = GONE
                    binding.materialDataGroup.visibility = GONE
                } else {
                    binding.binDataGroup.visibility = VISIBLE
                }
            }
        })
        Tools.attachButtonsToListener(
            this,
            binding.save,
        )
        setUpScannedSerialRecyclerView()

        EditTextActionHandler.OnEnterKeyPressed(binding.materialCode) {
            val materialCode = binding.materialCode.editText?.text.toString()
            if (materialCode.isNotEmpty()) {
                viewModel.getMaterialData(materialCode)
            } else {
                binding.materialCode.error =
                    getString(R.string.please_scan_or_enter_material_code)
            }
        }
        EditTextActionHandler.OnEnterKeyPressed(binding.serialNo) {
            val materialCode = binding.materialCode.editText?.text.toString().trim()
            val serialNo = binding.serialNo.editText?.text.toString().trim()
            if (materialCode.isNotEmpty()) {
                viewModel.checkSerial(materialCode, serialNo)
                readBarcode = 2
            } else {
                binding.serialNo.error = getString(R.string.please_scan_serials)
            }
        }
        observeBinCodeData()
        observeMaterialData()
        observeCheckSerialCode()
        observeSavingAuditDetails()
    }

    private fun observeSavingAuditDetails() {
        viewModel.saveAuditStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
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
        binding.materialCode.editText?.setText("")
        validMaterialCode = null
        selectedMaterial = null
        serialsList = mutableListOf()
        serialStringList = mutableListOf()
        serialsAdapter = SerialsWithRemoveButtonAdapter(serialsList, this)
        binding.serials.adapter = serialsAdapter
    }

    private fun observeCheckSerialCode() {
        var serialSuccess = false
        viewModel.checkSerialStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    serialSuccess = true
                }

                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.serialNo.error = it.message
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.serialLiveData.observe(requireActivity()) {
            if (serialSuccess) {
                serialsList.add(Serial(serial = it))
                serialStringList.add(it)
                serialsAdapter.notifyDataSetChanged()
                serialSuccess = false
            }
        }
    }

    private fun observeMaterialData() {
        viewModel.getMaterialDataStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.materialCode.error = it.message
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                }
            }
        }
        viewModel.getMaterialDataLiveData.observe(requireActivity()) {
            if (it != null) {
                fillMaterialData(it)
            } else {
                binding.materialCode.error = getString(R.string.wrong_material_code)
            }
        }
    }

    private fun observeBinCodeData() {
        viewModel.getBinDataLiveData.observe(requireActivity()) {
            if (it.isNotEmpty()) {
                fillBinData(it[0])
            } else {
                binding.binCode.error = getString(R.string.wrong_bin_code)
                binding.binDataGroup.visibility = GONE
            }
        }
        viewModel.getBinDataStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                else -> {
                    loadingDialog.dismiss()
                    binding.binCode.error = it.message
                }
            }
        }
    }

    private var validBinCode: String? = null
    private fun fillBinData(bin: Bin) {
        validBinCode = bin.storageBinCode!!
        binding.binCode.editText?.setText(bin.storageBinCode)
        binding.binDataGroup.visibility = VISIBLE
        val binData =
            "${bin.warehouseName} -> ${bin.storageLocationName} -> ${bin.storageSectionCode} -> ${bin.storageBinCode}"
        binding.locationDetails.text = binData
    }


    private lateinit var serialsAdapter: SerialsWithRemoveButtonAdapter
    private var serialsList = mutableListOf<Serial>()
    private var serialStringList = mutableListOf<String>()
    private fun setUpScannedSerialRecyclerView() {
        serialsAdapter = SerialsWithRemoveButtonAdapter(serialsList, this)
        binding.serials.adapter = serialsAdapter
    }

    private var selectedMaterial: MaterialData? = null

    var readBarcode = 0
    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedCode = barcodeReader.scannedData(p0!!)
            if (validBinCode == null) {
                viewModel.getBinData(scannedCode)
            } else if (selectedMaterial == null) {
                viewModel.getMaterialData(scannedCode)
            } else {
                readBarcode = 1
                viewModel.checkSerial(
                    binding.materialCode.editText?.text.toString(),
                    scannedCode
                )
            }
        }

    }

    private var validMaterialCode: String? = null
    private fun fillMaterialData(materialData: MaterialData) {
        validMaterialCode = materialData.materialCode!!
        selectedMaterial = materialData
        binding.let {
            it.materialCode.editText?.setText(materialData.materialCode)
            it.materialDesc.text = materialData.materialName
            it.materialDataGroup.visibility = VISIBLE
            if (selectedMaterial!!.isSerialized!!) {
                binding.scannedQty.text = "${serialsList.size}"
                it.serialNo.visibility = VISIBLE
                it.scannedQty.visibility = VISIBLE
                it.serials.visibility = VISIBLE
                it.countedQty.visibility = GONE
            } else {
                it.serialNo.visibility = GONE
                it.scannedQty.visibility = GONE
                it.serials.visibility = GONE
                it.countedQty.visibility = VISIBLE
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
        Tools.changeTitle(getString(R.string.start_audit), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if (validBinCode != null) {
                    if (validMaterialCode!!.isNotEmpty()) {
                        if (selectedMaterial!!.isSerialized!!) {
                            if (serialsList.isNotEmpty()) {
                                viewModel.saveAudit(
                                    materialCode = selectedMaterial!!.materialCode!!,
                                    serials = serialStringList,
                                    readBarcode = readBarcode,
                                    qty = serialStringList.size,
                                    binCode = validBinCode!!,
                                    auditHeaderId = auditOrder.auditHeaderId!!,
                                    projectNumber = selectedMaterial!!.materialCode!!,
                                )
                            } else {
                                binding.serialNo.error = getString(R.string.please_scan_serials)
                            }
                        } else {
                            val qty = binding.countedQty.editText?.text.toString().trim()
                            if (qty.isNotEmpty()) {
                                if (Tools.containsOnlyDigits(qty)) {
                                    viewModel.saveAudit(
                                        materialCode = selectedMaterial!!.materialCode!!,
                                        serials = serialStringList,
                                        readBarcode = readBarcode,
                                        qty = qty.toInt(),
                                        binCode = validBinCode!!,
                                        auditHeaderId = auditOrder.auditHeaderId!!,
                                        projectNumber = selectedMaterial!!.materialCode!!,
                                    )
                                } else {
                                    binding.countedQty.error =
                                        getString(R.string.please_enter_valid_quantity)
                                }
                            } else {
                                binding.countedQty.error = getString(R.string.please_enter_qty)
                            }
                        }
                    } else {
                        binding.materialCode.error =
                            getString(R.string.please_scan_valid_material_code)
                    }
                } else {
                    binding.binCode.error = getString(R.string.please_scan_valid_bin_code)
                }
            }
        }
    }

    override fun OnRemovedSerial(position: Int) {
        serialsList.removeAt(position)
        serialStringList.removeAt(position)
        serialsAdapter.notifyDataSetChanged()
    }
}