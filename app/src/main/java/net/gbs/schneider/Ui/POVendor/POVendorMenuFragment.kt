package net.gbs.schneider.Ui.POVendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.changeTitle
import net.gbs.schneider.databinding.FragmentPOVendorMenuBinding

class POVendorMenuFragment : BaseFragmentWithoutViewModel<FragmentPOVendorMenuBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPOVendorMenuBinding
        get() = FragmentPOVendorMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.receive.setOnClickListener {
            NavigateTo(it, R.id.action_POVendorMenuFragment_to_POVendorListFragment)
        }
        binding.putAway.setOnClickListener {
            NavigateTo(it, R.id.action_POVendorMenuFragment_to_POVendorListPutAwayFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.po_vendor_menu), requireActivity() as MainActivity)
    }
}