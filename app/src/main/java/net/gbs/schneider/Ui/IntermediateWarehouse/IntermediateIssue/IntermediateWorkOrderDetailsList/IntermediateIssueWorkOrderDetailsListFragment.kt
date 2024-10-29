package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateWorkOrderDetailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.IntentKeys
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.databinding.FragmentIntermediateIssueWorkOrderDetailsListBinding


class IntermediateIssueWorkOrderDetailsListFragment :
    BaseFragmentWithoutViewModel<FragmentIntermediateIssueWorkOrderDetailsListBinding>() {


    companion object {
        @JvmStatic
        fun newInstance() =
            IntermediateIssueWorkOrderDetailsListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateIssueWorkOrderDetailsListBinding
        get() = FragmentIntermediateIssueWorkOrderDetailsListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val workOrder = IntermediateWorkOrder.fromJson(
                requireArguments().getString(IntentKeys.WORK_ORDER_KEY).toString()
            )
            val adapter = IntermediateIssueWorkOrderDetailsAdapter(workOrder.materials)
            binding.itemsList.adapter = adapter
            binding.header.workOrderNumber.text = workOrder.workOrderNumber
            binding.header.projectTo.text = workOrder.projectNumber
        }

    }
}
