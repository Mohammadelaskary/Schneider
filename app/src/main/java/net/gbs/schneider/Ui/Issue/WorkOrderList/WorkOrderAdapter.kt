package net.gbs.schneider.Ui.Issue.WorkOrderList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.Model.WorkOrderType
import net.gbs.schneider.R
import net.gbs.schneider.databinding.WorkOrderIssueItemBinding
import java.util.Locale

class WorkOrderAdapter(
    private val onItemButtonsClicked: OnItemButtonsClicked,
    val context: Context
) : RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder>(),
    Filterable {
    var workOrdersListFiltered: List<WorkOrder> = listOf()
    var workOrdersList: List<WorkOrder> = listOf()
        set(value) {
            field = value
            workOrdersListFiltered = value
            notifyDataSetChanged()
        }

    inner class WorkOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = WorkOrderIssueItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkOrderViewHolder {
        val binding =
            WorkOrderIssueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkOrderViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrdersListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(holder: WorkOrderViewHolder, position: Int) {
        val workOrder = workOrdersListFiltered[position]
        with(holder) {
            binding.workOrderNumber.text = workOrder.workOrderNumber
            binding.projectTo.text = workOrder.projectNumberTo
            binding.workOrderType.text = if (workOrder.workOrderType == WorkOrderType.PRODUCTION) {
                context.getString(R.string.production)
            } else {
                context.getString(R.string.spare_parts)
            }
            itemView.setOnClickListener {
                lastSelectedPosition = currentSelectedPosition
                currentSelectedPosition = adapterPosition
                notifyItemChanged(position)
                notifyItemChanged(lastSelectedPosition)
            }
            if (currentSelectedPosition == position) {
                binding.buttonsGroup.visibility = RecyclerView.VISIBLE
            } else {
                binding.buttonsGroup.visibility = View.GONE
            }
            binding.startReceiving.setOnClickListener {
                onItemButtonsClicked.OnStartIssueingButtonClicked(workOrder)
            }
            binding.details.setOnClickListener {
                onItemButtonsClicked.OnDetailsButtonClicked(workOrder)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartIssueingButtonClicked(invoice: WorkOrder)
        fun OnDetailsButtonClicked(invoice: WorkOrder)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                workOrdersListFiltered = if (constraint.isEmpty()) {
                    workOrdersList
                } else {
                    val filteredList = mutableListOf<WorkOrder>()
//                    workOrdersList.filter {
//                        (it.workOrderNumber.contains(constraint)) or
//                                (it.projectNumber.contains(constraint))
//                    }.forEach { filteredList.add(it) }
                    workOrdersList.forEach {
                        if (it.workOrderNumber.lowercase(Locale.getDefault()).contains(constraint.toString()
                                .lowercase(Locale.getDefault())) || it.projectNumberTo.lowercase(Locale.getDefault()).contains(constraint.toString().lowercase(
                                Locale.getDefault()))){
                            filteredList.add(it)
                        }
                    }
                    filteredList
                }
                return FilterResults().apply { values = workOrdersListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                workOrdersListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<WorkOrder>
                }
                notifyDataSetChanged()
            }

        }
    }
}