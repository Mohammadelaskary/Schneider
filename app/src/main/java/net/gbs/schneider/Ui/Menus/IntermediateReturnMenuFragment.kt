package net.gbs.schneider.Ui.Menus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.databinding.FragmentIntermediateReturnMenuBinding


class IntermediateReturnMenuFragment :
    BaseFragmentWithoutViewModel<FragmentIntermediateReturnMenuBinding>() {

    companion object {

        @JvmStatic
        fun newInstance() =
            IntermediateReturnMenuFragment().apply {
            }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentIntermediateReturnMenuBinding
        get() = FragmentIntermediateReturnMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.receive.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_intermediateReturnMenuFragment_to_intermediateReceiveReturnWorkOrdersListFragment2)
        }
        binding.putAway.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_intermediateReturnMenuFragment_to_intermediatePutAwayReturnWorkOrdersListFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        Tools.showToolBar(activity as MainActivity)
        Tools.changeTitle(
            getString(R.string.intermediate_warehouse_return_menu),
            activity as MainActivity
        )
    }
}