package net.gbs.schneider.Ui.GetItemInfo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import net.gbs.schneider.Model.StockSerial
import net.gbs.schneider.databinding.SerialsListDialogBinding

class InfoSerialsListDialog(val serialsList: List<StockSerial>, context: Context) :
    Dialog(context) {
    lateinit var binding: SerialsListDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerialsListDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpSerialsRecyclerView()
    }

    private fun setUpSerialsRecyclerView() {
        Log.d("InfoSerialsListDialog", "setUpSerialsRecyclerView: ${serialsList.size}")
        val adapter = StockSerialsAdapter(serialsList)
        binding.serials.adapter = adapter
    }
}
