package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.SerialsAdapters.ShowStringSerialsAdapter
import net.gbs.schneider.databinding.SerializedMaterialListDialogLayoutBinding

class SerialsListDialog_Return(private val context: Context, val material: MaterialReturn) :
    Dialog(context) {

    private lateinit var binding: SerializedMaterialListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerializedMaterialListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillData()

    }

    private lateinit var adapter: ShowStringSerialsAdapter
    private fun setUpRecyclerView() {
        adapter = if (material.isBulk)
            ShowStringSerialsAdapter(listOf(material.issueWorkOrderSerials[0]))
        else
            ShowStringSerialsAdapter(material.issueWorkOrderSerials)
        binding.serials.adapter = adapter
    }


    private fun fillData() {
        binding.materialCode.text = material.materialCode
        binding.materialDesc.text = material.materialName
    }


    override fun onStart() {
        super.onStart()
        setUpRecyclerView()
    }

}