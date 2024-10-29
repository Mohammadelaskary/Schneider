package net.gbs.schneider.Ui.Issue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.databinding.FragmentIssueMenuBinding


class IssueMenuFragment : BaseFragmentWithoutViewModel<FragmentIssueMenuBinding>(),
    OnClickListener {
    companion object {
        @JvmStatic
        fun newInstance() =
            IssueMenuFragment().apply {

            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIssueMenuBinding
        get() = FragmentIssueMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachButtonsToListener(
            this,
            binding.workOrderIssue,
            binding.transfer,
            binding.issueReport
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.work_order_issue -> {
                NavigateTo(v, R.id.action_issueMenuFragment_to_workOrderListFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(
            mainTitle = getString(R.string.issue_menu),
            mainActivity = activity as MainActivity
        )
    }
}
