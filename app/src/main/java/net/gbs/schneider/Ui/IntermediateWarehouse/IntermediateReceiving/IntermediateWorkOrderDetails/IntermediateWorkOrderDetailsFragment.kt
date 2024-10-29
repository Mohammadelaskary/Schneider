package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateWorkOrderDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediateWorkOrderDetailsBinding


class IntermediateWorkOrderDetailsFragment :
    BaseFragmentWithoutViewModel<FragmentIntermediateWorkOrderDetailsBinding>() {

    companion object {

        @JvmStatic
        fun newInstance() =
            IntermediateWorkOrderDetailsFragment().apply {

            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateWorkOrderDetailsBinding
        get() = FragmentIntermediateWorkOrderDetailsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val workOrder = IntermediateWorkOrder.fromJson(
                requireArguments().getString(WORK_ORDER_KEY).toString()
            )
            val adapter = IntermediateWorkOrderDetailsAdapter(workOrder.materials)
            binding.itemsList.adapter = adapter
            binding.header.workOrderNumber.text = workOrder.workOrderNumber
            binding.header.projectTo.text = workOrder.projectNumber
        }

    }

    override fun onResume() {
        super.onResume()
        Tools.changeTitle(getString(R.string.work_order_details_list), activity as MainActivity)
        Tools.showToolBar(activity as MainActivity)
    }
}