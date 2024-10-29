package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway.IntermediatePutAwaySerialsListDialog_Return

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.IntermediateMaterialReturn
import net.gbs.schneider.databinding.SerializedMaterialListDialogLayoutBinding

class IntermediatePutAwaySerialsListDialog_Return(
    private val context: Context,
    val material: IntermediateMaterialReturn
) : Dialog(context) {

    private lateinit var binding: SerializedMaterialListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerializedMaterialListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillData()

    }

    private lateinit var adapter: IntermediatePutAwayReturnShowStringSerialsAdapter
    private fun setUpRecyclerView() {
        adapter = if (material.isBulk)
            IntermediatePutAwayReturnShowStringSerialsAdapter(listOf(material.returnedSerials[0]))
        else
            IntermediatePutAwayReturnShowStringSerialsAdapter(material.returnedSerials)
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