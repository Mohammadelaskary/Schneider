package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue.IntermediateIssueMaterialsListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.IntermediateIssueWorkOrderDetails
import net.gbs.schneider.databinding.WorkOrderDetailsListDialogLayoutBinding

class IntermediateIssueMaterialsListDialog(
    context: Context,
    private val materialsList: MutableList<IntermediateIssueWorkOrderDetails>,
    private val onIntermediateMaterialsItemClicked: IntermediateIssueMaterialsListAdapter.OnIntermediateMaterialsItemClicked
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
            IntermediateIssueMaterialsListAdapter(materialsList, onIntermediateMaterialsItemClicked)
        binding.workOrderDetailsList.adapter = adapter
    }
}