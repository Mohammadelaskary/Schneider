package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartChangingSerialsFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import net.gbs.schneider.IntentKeys.INVOICE_KEY
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.SerialStatus
import net.gbs.schneider.Model.SerialWithStatus
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_INFO
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.EditTextActionHandler
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.showSuccessAlerter
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.StartChangingSerials.ChangeSerialsAdapter
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListAdapter
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListDialog
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.StartChangingSerialsViewModel.StartChangingSerialsViewModel
import net.gbs.schneider.databinding.FragmentStartChangingSerialsBinding

class StartChangingSerialsFragment :
    BaseFragmentWithViewModel<StartChangingSerialsViewModel, FragmentStartChangingSerialsBinding>(),
    MaterialListAdapter.OnMaterialItemClicked, BarcodeListener, TriggerListener, OnClickListener,
    ChangeSerialsAdapter.OnRemoveCheckBoxClicked {

    companion object {
        fun newInstance() = StartChangingSerialsFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartChangingSerialsBinding
        get() = FragmentStartChangingSerialsBinding::inflate
    private lateinit var materialListDialog: MaterialListDialog
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        invoice = requireArguments().getString(INVOICE_KEY)?.let { Invoice.fromJson(it) }!!
        fillData()
        attachButtonsToListener(
            this,
            binding.save,
            binding.clearMaterialData,
            binding.materialList
        )
        clearInputLayoutError(binding.materialCode, binding.boxNumber, binding.serialNo)
        setUpScannedSerialRecyclerView()


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
                    if (material.isSerialized) {
                        selectedMaterial = material
                        fillMaterialData()
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.the_selected_item_is_not_serialized_item_please_select_serialized_item)
                        )
                    }
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            }
        }


        EditTextActionHandler.OnEnterKeyPressed(binding.serialNo) {
            val serialNo = binding.serialNo.editText?.text.toString().trim()
            val serial = selectedMaterial!!.serials.find { it.serial == serialNo }
            if (serial != null) {
                val scannedSerial = serialsList.find { it.serial == serialNo }
                if (scannedSerial == null) {
                    binding.serialNo.editText?.setText(serialNo)
                    serialsList.add(SerialWithStatus(serial.serial, SerialStatus.NEW))
                    serialsAdapter.notifyDataSetChanged()
                    binding.scannedQty.text = "${countOriginalAndAddedSerials(serialsList)}"
                } else {
                    binding.serialNo.error = getString(R.string.serial_added_before)
                }
            }

        }
        observeReceiving()
        watchMaterialCodeText()
        watchMaterialCodeText()
    }


    private fun watchMaterialCodeText() {
        binding.materialCode.editText?.doOnTextChanged { text, start, before, count ->
            if (text?.isEmpty()!!) {
                binding.clearMaterialData.visibility = View.INVISIBLE
                binding.materialList.visibility = VISIBLE
            } else {
                binding.clearMaterialData.visibility = VISIBLE
                binding.materialList.visibility = View.INVISIBLE
            }
        }
    }

    private fun observeReceiving() {
        viewModel.changeSerialsStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.hide()
                    showSuccessAlerter(it.message, requireActivity())
                    clearMaterialData()
                }

                else -> {
                    loadingDialog.hide()
                    warningDialog(requireContext(), it.message)
                    Log.d("StartChangingSerialsFragment", "observeReceiving: ${it.message}")
                }
            }
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.serialNo.editText?.setText("")
        binding.boxNumber.editText?.setText("")
        selectedMaterial = null
        serialsList.clear()
        serialsAdapter.notifyDataSetChanged()
    }

    private var originalSerialsList = listOf<Serial>()
    private lateinit var serialsAdapter: ChangeSerialsAdapter
    private val serialsList = mutableListOf<SerialWithStatus>()
    private fun setUpScannedSerialRecyclerView() {
        serialsAdapter = ChangeSerialsAdapter(serialsList, this)
        binding.scannedSerials.adapter = serialsAdapter
    }

    private var selectedMaterial: Material? = null

    override fun onMaterialItemClicked(material: Material) {
        if (isClickableMaterial) {
            if (USER_INFO!!.canSelectMaterial) {
                if (material.isSerialized) {
                    val serial = material.serials.find { it.isGenerated }
                    if (serial == null) {
                        selectedMaterial = material
                        fillMaterialData()
                        materialListDialog.dismiss()
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.can_not_change_generated_serials)
                        )
                    }
                } else {
                    warningDialog(
                        requireContext(),
                        getString(R.string.the_selected_item_is_not_serialized_item_please_select_serialized_item)
                    )
                }
            } else {
                warningDialog(
                    requireContext(),
                    getString(R.string.this_user_doesn_t_have_the_authority_to_select_item_code_please_scan_it)
                )
            }
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (selectedMaterial == null) {
                val material = invoice.materials.find { it.materialCode == scannedText }
                if (material != null) {
                    if (material.isSerialized) {
                        selectedMaterial = material
                        fillMaterialData()
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.the_selected_item_is_not_serialized_item_please_select_serialized_item)
                        )
                    }
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            } else {
                val scannedSerial = serialsList.find { it.serial == scannedText }
                if (scannedSerial == null) {
                    serialsList.add(SerialWithStatus(scannedText, SerialStatus.NEW))
                    serialsAdapter.notifyDataSetChanged()
                    binding.scannedQty.text = "${countOriginalAndAddedSerials(serialsList)}"
                } else {
                    binding.serialNo.error = getString(R.string.serial_added_before)
                }
            }
        }
    }

    private fun fillMaterialData() {
        originalSerialsList = selectedMaterial!!.serials
        selectedMaterial!!.serials.forEach {
            serialsList.add(SerialWithStatus(it.serial, SerialStatus.ORIGINAL))
        }
        serialsAdapter.notifyDataSetChanged()
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.boxNumber.editText?.setText(selectedMaterial!!.boxNumber)
            it.receivedQtyPerShippedQty.text = "${selectedMaterial!!.shippedQuantity}"
            it.scannedQty.text = "${countOriginalAndAddedSerials(serialsList)}"
            it.materialDataGroup.visibility = VISIBLE
            it.serialNo.visibility = VISIBLE
            it.scannedQty.visibility = VISIBLE
            it.scannedSerials.visibility = VISIBLE
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.start_changing_serials), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }


    var isClickableMaterial = false
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                if (selectedMaterial != null) {
                    val serialsListForSending = mutableListOf<SerialWithStatus>()
                    serialsList.forEach {
                        if (it.serialStatus == SerialStatus.NEW || it.serialStatus == SerialStatus.REMOVED) {
                            serialsListForSending.add(it)
                        }
                    }
                    if (serialsListForSending.isNotEmpty()) {
                        viewModel.changeSerials(
                            invoice = invoice,
                            selectedMaterial = selectedMaterial!!,
                            serials = serialsListForSending
                        )
                    } else {
                        warningDialog(requireContext(), getString(R.string.no_serials_to_update))
                    }
                } else {
                    binding.materialCode.error =
                        getString(R.string.please_scan_or_select_material_first)
                }
            }

            R.id.clear_material_data -> {
                clearMaterialData()
            }

            R.id.material_list -> {
                isClickableMaterial = true
                materialListDialog.show()

            }
        }
    }

    override fun onRemoveClicked(position: Int, status: SerialStatus) {
        val serialNo = serialsList[position].serial
        val originalSerial = originalSerialsList.find { it.serial == serialNo }
        if (originalSerial != null) {
            serialsList[position].serialStatus =
                if (serialsList[position].serialStatus == SerialStatus.ORIGINAL) SerialStatus.REMOVED else SerialStatus.ORIGINAL
            serialsAdapter.notifyDataSetChanged()
            binding.scannedQty.text = "${countOriginalAndAddedSerials(serialsList)}"
        } else {
            serialsList.removeAt(position)
            serialsAdapter.notifyDataSetChanged()
            binding.scannedQty.text = "${countOriginalAndAddedSerials(serialsList)}"
        }
    }

    private fun countOriginalAndAddedSerials(serialsList: List<SerialWithStatus>): Int {
        var count = 0
        serialsList.forEach {
            if (it.serialStatus == SerialStatus.ORIGINAL || it.serialStatus == SerialStatus.NEW)
                count++
        }
        return count
    }
}