package net.gbs.schneider.Ui.POPlant.Receiving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import net.gbs.schneider.Base.BaseFragmentWithoutViewModel
import net.gbs.schneider.Base.MainActivity
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Ui.Menus.MainWarehouseMenuFragment
import net.gbs.schneider.databinding.FragmentReceivingMenuBinding

class ReceivingMenuFragment : BaseFragmentWithoutViewModel<FragmentReceivingMenuBinding>(),
    OnClickListener {
    companion object {
        fun newInstance() = MainWarehouseMenuFragment()
        const val IS_PO_VENDOR_KEY = "isPoVendor"
    }

       override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReceivingMenuBinding
        get() = FragmentReceivingMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachButtonsToListener(
            this,
            binding.poVendor,
            binding.poPlant,
            binding.inspection,
            binding.putAway
        )
    }

    override fun onResume() {
        super.onResume()
        Tools.changeTitle(getString(R.string.receiving_menu), activity as MainActivity)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        when (v!!.id) {
            R.id.po_vendor ->  {
                bundle.putBoolean(IS_PO_VENDOR_KEY,true)
                NavigateTo(
                    v,
                    R.id.action_receivingMenuFragment_to_PO_InvoiceListFragment,bundle
                )
            }

            R.id.po_plant -> {
                bundle.putBoolean(IS_PO_VENDOR_KEY,false)
                NavigateTo(
                    v,
                    R.id.action_receivingMenuFragment_to_PO_InvoiceListFragment,bundle
                )
            }
        }
    }
}