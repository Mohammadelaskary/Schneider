package net.gbs.schneider.Ui.Return.ReturnWorkOrderDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.databinding.ReturnWorkOrderDetailsItemLayoutBinding

class ReturnWorkOrderDetailsAdapter(val workOrder: WorkOrderReturn) :
    RecyclerView.Adapter<ReturnWorkOrderDetailsAdapter.ReturnWorkOrderDetailsViewHolder>() {
    inner class ReturnWorkOrderDetailsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ReturnWorkOrderDetailsItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReturnWorkOrderDetailsViewHolder {
        val binding = ReturnWorkOrderDetailsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReturnWorkOrderDetailsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrder.materials.size
    }

    override fun onBindViewHolder(holder: ReturnWorkOrderDetailsViewHolder, position: Int) {
        val material = workOrder.materials[position]
        with(holder.binding) {
            workOrderNumber.text = workOrder.workOrderNumber
            projectNumber.text = workOrder.projectNumber
            materialCode.text = material.materialCode
            materialDesc.text = material.materialName
            shippedQty.text = material.returnedQuantity.toString()
        }
    }
}