package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.IntermediateIssueWorkOrderDetails
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.SerialsAdapters.ShowSerialsAdapters
import net.gbs.schneider.databinding.SerializedMaterialListDialogLayoutBinding

class IntermediateIssueSerialsListDialog(
    private val context: Context,
    val workOrderDetails: IntermediateIssueWorkOrderDetails
) : Dialog(context) {

    private lateinit var binding: SerializedMaterialListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerializedMaterialListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillData()

    }

    private lateinit var adapter: ShowSerialsAdapters
    private fun setUpRecyclerView() {
        adapter = ShowSerialsAdapters(workOrderDetails.issuedSerials)
        binding.serials.adapter = adapter
    }

    private fun fillData() {
        binding.materialCode.text = workOrderDetails.materialCode
        binding.materialDesc.text = workOrderDetails.materialName
    }

    fun setSerialAsScanned(serial: Serial) {
        workOrderDetails.issuedSerials.find { it.serial == serial.serial }?.isReceived =
            serial.isReceived
//        adapter.notifyDataSetChanged()
    }


    override fun onStart() {
        super.onStart()
        setUpRecyclerView()
    }
}