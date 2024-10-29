package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.ChangeQty.StartChangingSerialsFragment

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
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.ChangeQty.StartChangingSerialsViewModel.StartChangingQtyViewModel
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListAdapter
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListDialog
import net.gbs.schneider.databinding.FragmentStartChangingQtyBinding

class StartChangingQtyFragment :
    BaseFragmentWithViewModel<StartChangingQtyViewModel, FragmentStartChangingQtyBinding>(),
    MaterialListAdapter.OnMaterialItemClicked, BarcodeListener, TriggerListener, OnClickListener {

    companion object {
        fun newInstance() = StartChangingQtyFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartChangingQtyBinding
        get() = FragmentStartChangingQtyBinding::inflate
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
        clearInputLayoutError(binding.materialCode, binding.boxNumber, binding.qty)


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
                        if (material.isSerializedWithOneSerial) {
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
                                getString(R.string.can_not_change_qty)
                            )
                        }
                    } else {
                        selectedMaterial = material
                        fillMaterialData()
                        materialListDialog.dismiss()
                    }
                } else {
                    binding.materialCode.error = getString(R.string.wrong_material_code)
                }
            }
        }

        observeReceiving()
        watchMaterialCodeText()
        watchMaterialCodeText()
    }


    private fun watchMaterialCodeText() {
        binding.materialCode.editText?.doOnTextChanged { text, start, before, count ->
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
    }

    private fun observeReceiving() {
        viewModel.changeQtyStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.hide()
                    clearMaterialData()
                    showSuccessAlerter(it.message, requireActivity())
                }

                else -> {
                    loadingDialog.hide()
                    warningDialog(requireContext(), it.message)
                    Log.d("StartChangingQtyFragment", "observeReceiving: ${it.message}")
                }
            }
        }
    }

    private fun clearMaterialData() {
        binding.materialDataGroup.visibility = GONE
        binding.materialCode.editText?.setText("")
        binding.qty.editText?.setText("")
        binding.boxNumber.editText?.setText("")
        selectedMaterial = null
    }

    private var selectedMaterial: Material? = null

    override fun onMaterialItemClicked(material: Material) {
        if (isClickableMaterial) {
            if (USER_INFO!!.canSelectMaterial) {
                if (material.isSerialized) {
                    if (material.isSerializedWithOneSerial) {
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
                            getString(R.string.can_not_change_qty)
                        )
                    }
                } else {
                    selectedMaterial = material
                    fillMaterialData()
                    materialListDialog.dismiss()
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
            val material = invoice.materials.find { it.materialCode == scannedText }
            if (material != null) {
                if (material.isSerialized) {
                    if (material.isSerializedWithOneSerial) {
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
                            getString(R.string.can_not_change_qty)
                        )
                    }
                } else {
                    selectedMaterial = material
                    fillMaterialData()
                    materialListDialog.dismiss()
                }
            } else {
                binding.materialCode.error = getString(R.string.wrong_material_code)
            }
        }
    }

    private fun fillMaterialData() {
        binding.let {
            it.materialCode.editText?.setText(selectedMaterial!!.materialCode)
            it.materialDesc.text = selectedMaterial!!.materialName
            it.boxNumber.editText?.setText(selectedMaterial!!.boxNumber)
            it.shippedQty.text = "${selectedMaterial!!.shippedQuantity}"
            it.materialDataGroup.visibility = VISIBLE
            it.qty.visibility = VISIBLE
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {

    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.start_changing_qty), activity as MainActivity)
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
                    val qtyText = binding.qty.editText?.text.toString().trim()
                    var qty = 0
                    if (qtyText.isNotEmpty()) {
                        try {
                            qty = qtyText.toInt()
                            viewModel.changeSerials(
                                invoice = invoice,
                                selectedMaterial = selectedMaterial!!,
                                qty = qty
                            )
                        } catch (ex: Exception) {
                            binding.qty.error = getString(R.string.please_enter_valid_quantity)
                        }
                    } else {
                        warningDialog(
                            requireContext(),
                            getString(R.string.please_enter_valid_quantity)
                        )
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
}