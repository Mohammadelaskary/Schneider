package net.gbs.schneider.Ui.Audit.AuditOrdersList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.AuditOrder
import net.gbs.schneider.databinding.AuditOrderItemLayoutBinding

class AuditOrdersListAdapter(private val onAuditOrderItemClicked: OnAuditOrderItemClicked) :
    Adapter<AuditOrdersListAdapter.AuditOrdersListViewHolder>() {
    var auditList: List<AuditOrder> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class AuditOrdersListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AuditOrderItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            AuditOrdersListViewHolder {
        val binding =
            AuditOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuditOrdersListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return auditList.size
    }

    override fun onBindViewHolder(holder: AuditOrdersListViewHolder, position: Int) {
        val auditOrder = auditList[position]
        with(holder) {
            binding.auditNum.text = auditOrder.auditNo
//            binding.auditDate.text = auditOrder.auditDate
//            binding.warehouseName.text = auditOrder.warehouseName
//            binding.plant.text = auditOrder.plant
            itemView.setOnClickListener { onAuditOrderItemClicked.OnItemClicked(auditOrder) }
        }
    }

    interface OnAuditOrderItemClicked {
        fun OnItemClicked(audit: AuditOrder)
    }
}