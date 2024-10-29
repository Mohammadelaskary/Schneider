package net.gbs.schneider.Ui.GetSerialInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader.BarcodeListener
import com.honeywell.aidc.BarcodeReader.TriggerListener
import com.honeywell.aidc.TriggerStateChangeEvent
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.Model.Stock
import net.gbs.schneider.R
import net.gbs.schneider.Tools.BarcodeReader
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.databinding.FragmentSerialInfoBinding

class SerialInfoFragment :
    BaseFragmentWithViewModel<SerialInfoViewModel, FragmentSerialInfoBinding>(), BarcodeListener,
    TriggerListener {

    companion object {
        fun newInstance() = SerialInfoFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSerialInfoBinding
        get() = FragmentSerialInfoBinding::inflate

    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearInputLayoutError(binding.serialNo)
        binding.serialNo.setEndIconOnClickListener {
            val itemCode = binding.serialNo.editText?.text.toString().trim()
            if (itemCode.isNotEmpty()) {
                viewModel.getSerialInfo(itemCode)
            } else
                binding.serialNo.error = getString(R.string.please_scan_item_code)
        }
        setUpRecyclerView()
        observeItemCodeInfo()
    }

    private var stockList: List<Stock> = listOf()
    private lateinit var materialStockAdapter: MaterialStockAdapter
    private fun setUpRecyclerView() {
        materialStockAdapter = MaterialStockAdapter(requireContext())
        binding.itemList.adapter = materialStockAdapter
    }

    private fun observeItemCodeInfo() {
        viewModel.getSerialInfoStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.serialNo.error = it.message
                    materialStockAdapter.stockList = listOf()
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                    materialStockAdapter.stockList = listOf()
                }
            }
        }
        viewModel.getSerialInfoLiveData.observe(requireActivity()) {
            stockList = it
            materialStockAdapter.stockList = stockList
            materialStockAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.serial_info), requireActivity() as MainActivity)
        barcodeReader.onResume()
    }

    override fun onPause() {
        super.onPause()
        barcodeReader.onPause()
    }

    override fun onBarcodeEvent(p0: BarcodeReadEvent?) {
        requireActivity().runOnUiThread {
            val scannedText = barcodeReader.scannedData(p0!!)
            if (scannedText.isNotEmpty()) {
                binding.serialNo.editText?.setText(scannedText)
                viewModel.getSerialInfo(scannedText)
            } else
                binding.serialNo.error = getString(R.string.please_scan_item_code)
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        TODO("Not yet implemented")
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }
}