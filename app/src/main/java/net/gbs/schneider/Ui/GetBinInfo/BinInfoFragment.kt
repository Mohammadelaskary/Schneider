package net.gbs.schneider.Ui.GetBinInfo

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
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Ui.GetItemInfo.MaterialStockAdapter
import net.gbs.schneider.databinding.FragmentBinInfoBinding

class BinInfoFragment : BaseFragmentWithViewModel<BinInfoViewModel, FragmentBinInfoBinding>(),
    BarcodeListener, TriggerListener {

    companion object {
        fun newInstance() = BinInfoFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBinInfoBinding
        get() = FragmentBinInfoBinding::inflate
    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Tools.clearInputLayoutError(binding.binCode)
        binding.binCode.setEndIconOnClickListener {
            val binCode = binding.binCode.editText?.text.toString().trim()
            if (binCode.isNotEmpty()) {
                viewModel.getBinInfo(binCode)
            } else
                binding.binCode.error = getString(R.string.please_scan_or_enter_bin_code)
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
        viewModel.getBinInfoStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.binCode.error = it.message
                    materialStockAdapter.stockList = listOf()
                }

                else -> {
                    loadingDialog.dismiss()
                    Tools.warningDialog(requireContext(), it.message)
                    materialStockAdapter.stockList = listOf()
                }
            }
        }
        viewModel.getBinInfoLiveData.observe(requireActivity()) {
            stockList = it
            materialStockAdapter.stockList = stockList
            materialStockAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        Tools.changeTitle(getString(R.string.bin_info), requireActivity() as MainActivity)
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
                binding.binCode.editText?.setText(scannedText)
                viewModel.getBinInfo(scannedText)
            } else
                binding.binCode.error = getString(R.string.please_scan_item_code)
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        TODO("Not yet implemented")
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }
}