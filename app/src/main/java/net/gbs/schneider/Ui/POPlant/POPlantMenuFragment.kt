package net.gbs.schneider.Ui.POPlant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.databinding.FragmentPOPlantMenuBinding

class POPlantMenuFragment : BaseFragmentWithoutViewModel<FragmentPOPlantMenuBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPOPlantMenuBinding
        get() = FragmentPOPlantMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.changeSerials.setOnClickListener {
            NavigateTo(it, R.id.action_POPlantMenuFragment_to_changeSerialsPO_InvoiceListFragment)
        }
        binding.receive.setOnClickListener {
            NavigateTo(it, R.id.action_POPlantMenuFragment_to_receivingMenuFragment2)
        }
        binding.putAway.setOnClickListener {
            NavigateTo(it, R.id.action_POPlantMenuFragment_to_invoiceListPutAwayFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.po_factory_menu), activity as MainActivity)
    }
}