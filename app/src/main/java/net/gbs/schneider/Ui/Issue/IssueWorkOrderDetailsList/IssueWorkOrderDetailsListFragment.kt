package net.gbs.schneider.Ui.Issue.IssueWorkOrderDetailsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.WORK_ORDER_KEY
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.databinding.FragmentIssueWorkOrderDetailsListBinding


class IssueWorkOrderDetailsListFragment :
    BaseFragmentWithoutViewModel<FragmentIssueWorkOrderDetailsListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIssueWorkOrderDetailsListBinding
        get() = FragmentIssueWorkOrderDetailsListBinding::inflate
    lateinit var workOrder: WorkOrder
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            workOrder = WorkOrder.fromJson(arguments?.getString(WORK_ORDER_KEY)!!)
            setUpRecyclerView()
        }
    }

    lateinit var adapter: IssueWorkOrderDetailsAdapter
    private fun setUpRecyclerView() {
        adapter = IssueWorkOrderDetailsAdapter(workOrder)
        binding.detailsList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.issue_work_order_details_list), activity as MainActivity)
    }
}