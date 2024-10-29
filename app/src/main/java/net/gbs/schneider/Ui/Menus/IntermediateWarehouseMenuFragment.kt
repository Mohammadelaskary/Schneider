package net.gbs.schneider.Ui.Menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.databinding.FragmentIntermediateWarehouseMenuBinding


class IntermediateWarehouseMenuFragment :
    BaseFragmentWithoutViewModel<FragmentIntermediateWarehouseMenuBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            IntermediateWarehouseMenuFragment().apply {

            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateWarehouseMenuBinding
        get() = FragmentIntermediateWarehouseMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.receiving.setOnClickListener {
            NavigateTo(
                requireView(),
                R.id.action_intermediateWarehouseMenuFragment_to_intermediate_receive_nav_graph
            )
        }
        binding.issue.setOnClickListener {
            NavigateTo(
                requireView(),
                R.id.action_intermediateWarehouseMenuFragment_to_intermediateIssueWorkOrdersListFragment
            )
        }
        binding.returnButton.setOnClickListener {
            NavigateTo(
                requireView(),
                R.id.action_intermediateWarehouseMenuFragment_to_intermediateReturnMenuFragment
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Tools.showToolBar(activity as MainActivity)
        Tools.changeTitle(getString(R.string.intermediate_warehouse), activity as MainActivity)
    }
}