package net.gbs.schneider.Ui.Return

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
import net.gbs.schneider.databinding.FragmentReturnMenuBinding

class ReturnMenuFragment : BaseFragmentWithoutViewModel<FragmentReturnMenuBinding>(),
    OnClickListener {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReturnMenuBinding
        get() = FragmentReturnMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachButtonsToListener(this, binding.returnButton, binding.putAway)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ReturnMenuFragment().apply {

            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.return_button -> NavigateTo(
                v,
                R.id.action_returnMenuFragment_to_invoiceListReturnFragment
            )

            R.id.put_away -> NavigateTo(
                v,
                R.id.action_returnMenuFragment_to_putAwayInvoiceListReturnFragment
            )
        }
    }

    override fun onResume() {
        super.onResume()
        changeTitle(getString(R.string.return_menu), activity as MainActivity)
    }
}