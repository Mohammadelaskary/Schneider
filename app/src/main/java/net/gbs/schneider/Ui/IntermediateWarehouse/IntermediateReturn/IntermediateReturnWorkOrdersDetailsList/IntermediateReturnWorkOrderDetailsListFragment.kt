package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReturnWorkOrdersDetailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.databinding.FragmentReturnWorkOrderDetailsListBinding

class IntermediateReturnWorkOrderDetailsListFragment :
    BaseFragmentWithoutViewModel<FragmentReturnWorkOrderDetailsListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReturnWorkOrderDetailsListBinding
        get() = FragmentReturnWorkOrderDetailsListBinding::inflate
    lateinit var workOrder: IntermediateWorkOrderReturn
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            workOrder = IntermediateWorkOrderReturn.fromJson(arguments?.getString(WORK_ORDER_KEY)!!)
            setUpRecyclerView()
        }
    }

    lateinit var adapter: IntermediateReturnWorkOrderDetailsAdapter
    private fun setUpRecyclerView() {
        adapter = IntermediateReturnWorkOrderDetailsAdapter(workOrder)
        binding.workOrderDetailsList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.work_order_details_list), activity as MainActivity)
    }
}