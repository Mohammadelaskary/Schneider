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
import net.gbs.schneider.databinding.FragmentMainMainMenuBinding

class MainMainMenuFragment : BaseFragmentWithoutViewModel<FragmentMainMainMenuBinding>() {

    companion object {
        fun newInstance() = MainMainMenuFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainMainMenuBinding
        get() = FragmentMainMainMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainWarehouse.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_mainMainMenuFragment_to_main_warehouse_nav_graph)
        }
        binding.intermediateWarehouse.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_mainMainMenuFragment_to_intermediate_warehouse_nav_graph)
        }
        binding.binInfo.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_mainMainMenuFragment_to_binInfoFragment2)
        }
        binding.itemInfo.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_mainMainMenuFragment_to_itemInfoFragment2)
        }
        binding.serialInfo.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_mainMainMenuFragment_to_serialInfoFragment2)
        }
    }

    override fun onResume() {
        super.onResume()
        Tools.showToolBar(activity as MainActivity)
        Tools.changeTitle(getString(R.string.main_menu), activity as MainActivity)
    }
}