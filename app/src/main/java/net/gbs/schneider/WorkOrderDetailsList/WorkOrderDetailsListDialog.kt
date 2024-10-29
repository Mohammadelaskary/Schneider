package net.gbs.schneider.WorkOrderDetailsList

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.WorkOrderDetails
import net.gbs.schneider.databinding.WorkOrderDetailsListDialogLayoutBinding

class WorkOrderDetailsListDialog(context: Context, private val workOrderDetailsList: MutableList<WorkOrderDetails>, private val onWorkOrderDetailsItemClicked: WorkOrderDetailsListAdapter.OnWorkOrderDetailsItemClicked) :Dialog(context) {
    private lateinit var binding: WorkOrderDetailsListDialogLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkOrderDetailsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    private fun setUpMaterialsRecyclerView() {

        val adapter = WorkOrderDetailsListAdapter(workOrderDetailsList,onWorkOrderDetailsItemClicked)
        binding.workOrderDetailsList.adapter = adapter
    }
}