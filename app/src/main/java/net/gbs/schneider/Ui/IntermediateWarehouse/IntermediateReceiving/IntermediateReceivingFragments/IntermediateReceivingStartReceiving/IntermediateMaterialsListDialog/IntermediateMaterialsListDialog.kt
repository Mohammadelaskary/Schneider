package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving.IntermediateMaterialsListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.databinding.WorkOrderDetailsListDialogLayoutBinding

class IntermediateMaterialsListDialog(
    context: Context,
    private val materialsList: MutableList<IntermediateMaterial>,
    private val onIntermediateMaterialsItemClicked: IntermediateMaterialsListAdapter.OnIntermediateMaterialsItemClicked
) : Dialog(context) {
    private lateinit var binding: WorkOrderDetailsListDialogLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkOrderDetailsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    private fun setUpMaterialsRecyclerView() {
        val adapter =
            IntermediateMaterialsListAdapter(materialsList, onIntermediateMaterialsItemClicked)
        binding.workOrderDetailsList.adapter = adapter
    }
}