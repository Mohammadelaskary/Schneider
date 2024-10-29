package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateFastPutAway

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayProductionWorkOrderBody
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediateFastPutAwayBinding

class IntermediateFastPutAwayFragment :
    BaseFragmentWithViewModel<IntermediateFastPutAwayViewModel, FragmentIntermediateFastPutAwayBinding>(),
    OnClickListener, BarcodeListener, TriggerListener,
    IntermediateFastPutAwayItemAdapter.OnFastPutAwayItemClicked {

    companion object {
        fun newInstance() = IntermediateFastPutAwayFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateFastPutAwayBinding
        get() = FragmentIntermediateFastPutAwayBinding::inflate

    private lateinit var workOrder: IntermediateWorkOrder
    private lateinit var barcodeReader: BarcodeReader
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Tools.clearInputLayoutError(binding.binCode)
        if (arguments != null) {
            workOrder =
                IntermediateWorkOrder.fromJson(arguments?.getString(IntentKeys.WORK_ORDER_KEY)!!)
            fillData()
            setUpRecyclerView()
        }
        binding.save.setOnClickListener(this)
        observeGettingBinCodeData()
        observeFastPutAway()
        binding.selectAll.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                workOrder.materials.forEach {
                    it.isSelected = true
                    if (!selectedItemsIds.contains(it.workOrderIssueDetailsId))
                        selectedItemsIds.add(it.workOrderIssueDetailsId)
                    compoundButton.isEnabled = false
                }
                selectedItemsIds.forEach {
                    Log.d(ContentValues.TAG, "onItemClicked: ${it}")
                }
                materialAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeFastPutAway() {
        viewModel.fastPutAwayStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    Tools.showSuccessAlerter(it.message, requireActivity())
                    Tools.back(this)
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                }
            }
        }
    }

    private fun observeGettingBinCodeData() {
        viewModel.getBinCodeStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
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
        viewModel.getBinCodeLiveData.observe(requireActivity()) {
            try {
                binding.binCode.editText?.setText(it[0].storageBinCode)
            } catch (ex: Exception) {
                Tools.warningDialog(requireContext(), ex.message.toString())
            }

        }
    }

    private lateinit var materialAdapter: IntermediateFastPutAwayItemAdapter
    private fun setUpRecyclerView() {
        materialAdapter =
            IntermediateFastPutAwayItemAdapter(workOrder.materials, requireContext(), this)
        binding.recyclerView.adapter = materialAdapter
    }

    private fun fillData() {
        with(binding) {
            plant.text = viewModel.getPlantFromLocalStorage()?.plantName
            warehouse.text = viewModel.getWarehouseFromLocalStorage()?.warehouseName
            header.workOrderNumber.text = workOrder.workOrderNumber
            header.projectTo.text = workOrder.projectNumber

        }
    }

    private val selectedItemsIds: MutableList<Int> = mutableListOf()

    override fun onItemClicked(position: Int, isSelected: Boolean) {
        workOrder.materials[position].isSelected = !isSelected
        materialAdapter.notifyDataSetChanged()

        if (isSelected) {
            binding.selectAll.isChecked = false
            selectedItemsIds.remove(element = workOrder.materials[position].workOrderIssueDetailsId)
            binding.selectAll.isEnabled = true
        } else {
            if (!selectedItemsIds.contains(workOrder.materials[position].workOrderIssueDetailsId))
                selectedItemsIds.add(workOrder.materials[position].workOrderIssueDetailsId)
            binding.selectAll.isChecked = selectedItemsIds.size == workOrder.materials.size
        }

        selectedItemsIds.forEach {
            Log.d(ContentValues.TAG, "onItemClicked: ${it}")
        }
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            try {
                val scannedText = barcodeReader.scannedData(p0!!)
                if (scannedText.isNotEmpty()) {
                    viewModel.getBinCodeData(scannedText)
                } else {
                    binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
                }
            } catch (ex: Exception) {
                Tools.warningDialog(requireContext(), ex.message.toString())
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
        Tools.showToolBar(activity as MainActivity)
        Tools.changeTitle(getString(R.string.fast_put_away), activity as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.save -> {
                val binCode = binding.binCode.editText?.text.toString().trim()
                if (readyToSave(binCode)) {
                    val body = FastPutAwayProductionWorkOrderBody(
                        isFullPutaway = binding.selectAll.isChecked,
                        storageBinCode = binCode,
                        workOrderIssueId = workOrder.workOrderIssueId,
                        workOrderIssueDetailsId = selectedItemsIds
                    )
                    viewModel.fastPutAway(body)
                }
            }
        }
    }

    private fun readyToSave(binCode: String): Boolean {
        var isReady = true
        if (binCode.isEmpty()) {
            isReady = false
            binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
        }
        if (selectedItemsIds.isEmpty()) {
            isReady = false
            Tools.warningDialog(
                requireContext(),
                getString(R.string.please_select_items_to_put_away)
            )
        }
        return isReady
    }

}