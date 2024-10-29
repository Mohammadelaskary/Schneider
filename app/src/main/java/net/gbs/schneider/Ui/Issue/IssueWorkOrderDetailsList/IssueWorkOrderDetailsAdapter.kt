package net.gbs.schneider.Ui.Issue.IssueWorkOrderDetailsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.databinding.IssueWorkOrderDetailsItemBinding

class IssueWorkOrderDetailsAdapter(val workOrder: WorkOrder) :
    Adapter<IssueWorkOrderDetailsAdapter.IssueWorkOrderDetailsListViewHolder>() {
    inner class IssueWorkOrderDetailsListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = IssueWorkOrderDetailsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IssueWorkOrderDetailsListViewHolder {
        val binding = IssueWorkOrderDetailsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IssueWorkOrderDetailsListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrder.workOrderDetails.size
    }

    override fun onBindViewHolder(holder: IssueWorkOrderDetailsListViewHolder, position: Int) {
        val workOrderDetails = workOrder.workOrderDetails[position]
        holder.binding.workOrderNumber.text = workOrder.workOrderNumber
        holder.binding.projectTo.text = workOrder.projectNumberTo
        holder.binding.materialCode.text = workOrderDetails.materialCode
        holder.binding.materialDesc.text = workOrderDetails.materialName
        holder.binding.workOrderDetailQty.text = workOrderDetails.workOrderDetailQuantity.toString()
    }
}