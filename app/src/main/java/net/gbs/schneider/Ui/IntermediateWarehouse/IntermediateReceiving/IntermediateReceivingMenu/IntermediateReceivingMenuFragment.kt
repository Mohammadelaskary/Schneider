package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.databinding.FragmentIntermediateReceivingMenuBinding


class IntermediateReceivingMenuFragment :
    BaseFragmentWithoutViewModel<FragmentIntermediateReceivingMenuBinding>() {

    companion object {

        @JvmStatic
        fun newInstance() =
            IntermediateReceivingMenuFragment().apply {

            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateReceivingMenuBinding
        get() = FragmentIntermediateReceivingMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.receiving.setOnClickListener {
            NavigateTo(
                requireView(),
                R.id.action_intermediateReceivingMenuFragment_to_receivingWorkOrderListFragment
            )
        }
        binding.putAway.setOnClickListener {
            NavigateTo(
                requireView(),
                R.id.action_intermediateReceivingMenuFragment_to_putAwayWorkOrderListFragment
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Tools.changeTitle(getString(R.string.receiving_menu), activity as MainActivity)
        Tools.showToolBar(activity as MainActivity)
    }
}