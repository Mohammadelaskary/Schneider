package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateIssueWorkOrdersList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.gbs.schneider.Model.IntermediateIssueWorkOrder
import net.gbs.schneider.databinding.IntermediateIssueWorkOrderItemBinding

class IntermediateIssueWorkOrdersAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    RecyclerView.Adapter<IntermediateIssueWorkOrdersAdapter.IntermediateIssueWorkOrdersViewHolder>(),
    Filterable {

    inner class IntermediateIssueWorkOrdersViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = IntermediateIssueWorkOrderItemBinding.bind(itemView)
    }

    private var workOrdersListFiltered: List<IntermediateIssueWorkOrder> = mutableListOf()
    var workOrdersList: List<IntermediateIssueWorkOrder> = mutableListOf()
        set(value) {
            field = value
            workOrdersListFiltered = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateIssueWorkOrdersViewHolder {
        val binding = IntermediateIssueWorkOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediateIssueWorkOrdersViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrdersListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(
        holder: IntermediateIssueWorkOrdersViewHolder,
        position: Int
    ) {
        val workOrder = workOrdersListFiltered[position]
        Log.d(
            "IntermediateStartIssueFragment-fillData",
            "fillData: ${IntermediateIssueWorkOrder.toJson(workOrder)}"
        )
        with(holder) {
            binding.workOrderNumber.text = workOrder.workOrderNumber
            binding.projectTo.text = workOrder.projectNumberTo
//            itemView.setOnClickListener {
//                lastSelectedPosition = currentSelectedPosition
//                currentSelectedPosition = adapterPosition
//                notifyItemChanged(position)
//                notifyItemChanged(lastSelectedPosition)
//            }
//            if (currentSelectedPosition == position){
//                binding.buttonsGroup.visibility = RecyclerView.VISIBLE
//            } else {
//                binding.buttonsGroup.visibility = View.GONE
//            }
            itemView.setOnClickListener {
                onItemButtonsClicked.OnStartIssueButtonClicked(workOrder)
            }
//            binding.details.setOnClickListener {
//                onItemButtonsClicked.OnDetailsButtonClicked(workOrder)
//            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartIssueButtonClicked(workOrder: IntermediateIssueWorkOrder)
        fun OnDetailsButtonClicked(workOrder: IntermediateIssueWorkOrder)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                workOrdersListFiltered = if (charString.isEmpty()) {
                    workOrdersList
                } else {
                    val filteredList = mutableListOf<IntermediateIssueWorkOrder>()
                    workOrdersList.filter {
                        (it.workOrderNumber?.contains(charString)!!
                                or it.projectNumberTo?.contains(charString)!!)

                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = workOrdersListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                workOrdersListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<IntermediateIssueWorkOrder>
                }
                notifyDataSetChanged()
            }

        }
    }
}