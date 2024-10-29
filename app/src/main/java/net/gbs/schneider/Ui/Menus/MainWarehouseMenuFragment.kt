package net.gbs.schneider.Ui.Menus

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
import net.gbs.schneider.Tools.Tools.showToolBar
import net.gbs.schneider.databinding.FragmentMainMenuBinding


class MainWarehouseMenuFragment : BaseFragmentWithoutViewModel<FragmentMainMenuBinding>(),
    OnClickListener {
    companion object {
        fun newInstance() = MainWarehouseMenuFragment()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainMenuBinding
        get() = FragmentMainMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachButtonsToListener(
            this,
            binding.receiving,
            binding.issue,
            binding.returnButton,
            binding.audit,
        )
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.receiving -> NavigateTo(v, R.id.action_mainMenuFragment2_to_POPlantMenuFragment)
            R.id.issue -> NavigateTo(v, R.id.action_mainMenuFragment2_to_issueMenuFragment)
            R.id.audit -> NavigateTo(v, R.id.action_mainMenuFragment2_to_auditOrdersListFragment)
            R.id.return_button -> NavigateTo(v, R.id.action_mainMenuFragment2_to_returnMenuFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        showToolBar(activity as MainActivity)
        changeTitle(getString(R.string.main_warehouse), activity as MainActivity)

    }
}