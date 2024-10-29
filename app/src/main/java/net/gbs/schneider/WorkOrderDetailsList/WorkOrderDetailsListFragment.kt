package net.gbs.schneider.WorkOrderDetailsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gbs.schneider.R

class WorkOrderDetailsListFragment : Fragment() {

    companion object {
        fun newInstance() = WorkOrderDetailsListFragment()
    }

    private lateinit var viewModel: WorkOrderDetailsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_order_details_list, container, false)
    }



}