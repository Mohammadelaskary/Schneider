package net.gbs.schneider.Return.Receive.InvoiceList.IntermediatePutAwayReturnWorkOrdersListAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.databinding.PutAwayReturnInvoiceItemLayoutBinding

class IntermediatePutAwayReturnWorkOrdersListAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    RecyclerView.Adapter<IntermediatePutAwayReturnWorkOrdersListAdapter.IntermediatePutAwayReturnWorkOrdersListViewHolder>(),
    Filterable {
    var workOrdersListFiltered: List<IntermediateWorkOrderReturn> = listOf()
    var workOrdersList: List<IntermediateWorkOrderReturn> = listOf()
        set(value) {
            field = value
            workOrdersListFiltered = value
            notifyDataSetChanged()
        }

    inner class IntermediatePutAwayReturnWorkOrdersListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = PutAwayReturnInvoiceItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediatePutAwayReturnWorkOrdersListViewHolder {
        val binding = PutAwayReturnInvoiceItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediatePutAwayReturnWorkOrdersListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrdersListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(
        holder: IntermediatePutAwayReturnWorkOrdersListViewHolder,
        position: Int
    ) {
        val workOrder = workOrdersListFiltered[position]
        with(holder) {
            binding.projectNumber.text = workOrder.projectNumber.toString()
            binding.workOrderNumber.text = workOrder.workOrderNumber.toString()
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
            binding.startPutAway.setOnClickListener {
                onItemButtonsClicked.OnStartReturnButtonClicked(workOrder)
            }
            binding.details.setOnClickListener {
                onItemButtonsClicked.OnDetailsButtonClicked(workOrder)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartReturnButtonClicked(workOrder: IntermediateWorkOrderReturn)
        fun OnDetailsButtonClicked(workOrder: IntermediateWorkOrderReturn)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                workOrdersListFiltered = if (charString.isEmpty()) {
                    workOrdersList
                } else {
                    val filteredList = mutableListOf<IntermediateWorkOrderReturn>()
                    workOrdersList.filter {
                        (it.workOrderNumber.contains(constraint!!)) or
                                (it.projectNumber.contains(constraint))
                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = workOrdersListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                workOrdersListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<IntermediateWorkOrderReturn>
                }
                notifyDataSetChanged()
            }

        }
    }
}