package net.gbs.schneider.WorkOrderDetailsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.WorkOrderDetails
import net.gbs.schneider.databinding.WorkOrderDetailsItemBinding

class WorkOrderDetailsListAdapter(private val workOrderDetailsList: MutableList<WorkOrderDetails>, val onWorkOrderDetailsItemClicked: OnWorkOrderDetailsItemClicked): Adapter<WorkOrderDetailsListAdapter.WorkOrderDetailsViewHolder>() {

    inner class WorkOrderDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = WorkOrderDetailsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderDetailsViewHolder {
        val binding = WorkOrderDetailsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return WorkOrderDetailsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrderDetailsList.size
    }

    override fun onBindViewHolder(holder: WorkOrderDetailsViewHolder, position: Int) {
        val workOrderDetailsItem = workOrderDetailsList[position]
        with(holder){
            binding.materialCode.text = workOrderDetailsItem.materialCode
            binding.materialDesc.text = workOrderDetailsItem.materialName
//            binding.projectDesc.text = workOrderDetailsItem.projectDesc
//            binding.location.text = workOrderDetailsItem.location.toString()
            binding.receivedQtyPerShippedQty.text = "${workOrderDetailsItem.issuedQuantity} / ${workOrderDetailsItem.workOrderDetailQuantity}"
            itemView.setOnClickListener {
                onWorkOrderDetailsItemClicked.onWorkOrderDetailsItemClicked(workOrderDetailsItem)
            }
        }
    }

    interface OnWorkOrderDetailsItemClicked {
        fun onWorkOrderDetailsItemClicked(workOrder: WorkOrderDetails)
    }
}