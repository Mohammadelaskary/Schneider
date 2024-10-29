package net.gbs.schneider.Ui.Audit.AuditOrdersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.IntentKeys.AUDIT_ORDER_KEY
import net.gbs.schneider.Model.AuditOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.Tools.Tools.showErrorTextView
import net.gbs.schneider.databinding.FragmentAuditOrdersListBinding

class AuditOrdersListFragment :
    BaseFragmentWithViewModel<AuditOrdersListViewModel, FragmentAuditOrdersListBinding>(),
    AuditOrdersListAdapter.OnAuditOrderItemClicked {

    companion object {
        fun newInstance() = AuditOrdersListFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuditOrdersListBinding
        get() = FragmentAuditOrdersListBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAuditOrdersRecyclerView()

        observeGettingAuditOrdersHeaders()
    }

    private fun observeGettingAuditOrdersHeaders() {
        viewModel.getAuditHeader.observe(requireActivity()) {
            adapter.auditList = it
        }
        viewModel.getAuditHeaderStatus.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                    binding.errorMessage.visibility = GONE
                    adapter.auditList = listOf()
                }

                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    binding.errorMessage.visibility = GONE
                }

                else -> {
                    loadingDialog.dismiss()
                    showErrorTextView(it.message, binding.errorMessage)
                    adapter.auditList = listOf()
                }
            }
        }
    }

    private lateinit var adapter: AuditOrdersListAdapter
    private fun setAuditOrdersRecyclerView() {
        adapter =
            AuditOrdersListAdapter(
                this
            )
        binding.auditList.adapter = adapter
    }

    override fun OnItemClicked(audit: AuditOrder) {
        val bundle = Bundle()
        bundle.putString(AUDIT_ORDER_KEY, AuditOrder.toJson(audit))
        NavigateTo(requireView(), R.id.action_auditOrdersListFragment_to_startAuditFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAuditOrdersList()
        changeTitle(getString(R.string.audit_list), activity as MainActivity)
    }
}