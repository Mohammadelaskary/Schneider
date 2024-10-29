package net.gbs.schneider.Ui.GetItemInfo

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
import net.gbs.schneider.databinding.FragmentItemInfoBinding

class ItemInfoFragment : BaseFragmentWithViewModel<ItemInfoViewModel, FragmentItemInfoBinding>(),
    BarcodeListener, TriggerListener {

    companion object {
        fun newInstance() = ItemInfoFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemInfoBinding
        get() = FragmentItemInfoBinding::inflate

    private lateinit var barcodeReader: BarcodeReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barcodeReader = BarcodeReader(this, this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearInputLayoutError(binding.itemCode)
        binding.itemCode.setEndIconOnClickListener {
            val itemCode = binding.itemCode.editText?.text.toString().trim()
            if (itemCode.isNotEmpty()) {
                viewModel.getItemInfo(itemCode)
            } else
                binding.itemCode.error = getString(R.string.please_scan_item_code)
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
        viewModel.getItemInfoStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> loadingDialog.dismiss()
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    binding.itemCode.error = it.message
                    materialStockAdapter.stockList = listOf()
                }

                else -> {
                    loadingDialog.dismiss()
                    warningDialog(requireContext(), it.message)
                    materialStockAdapter.stockList = listOf()
                }
            }
        }
        viewModel.getItemInfoLiveData.observe(requireActivity()) {
            stockList = it
            materialStockAdapter.stockList = stockList
            materialStockAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.item_info), requireActivity() as MainActivity)
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
                binding.itemCode.editText?.setText(scannedText)
                viewModel.getItemInfo(scannedText)
            } else
                binding.itemCode.error = getString(R.string.please_scan_item_code)
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        TODO("Not yet implemented")
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        barcodeReader.onTrigger(p0!!)
    }
}