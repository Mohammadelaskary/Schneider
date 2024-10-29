package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediatePutAwayWorkOrdersList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.databinding.IntermediatePutAwayWorkOrderItemBinding

class IntermediatePutAwayWorkOrdersAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    RecyclerView.Adapter<IntermediatePutAwayWorkOrdersAdapter.IntermediatePutAwayWorkOrdersViewHolder>(),
    Filterable {

    inner class IntermediatePutAwayWorkOrdersViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = IntermediatePutAwayWorkOrderItemBinding.bind(itemView)
    }

    private var workOrdersListFiltered: List<IntermediateWorkOrder> = mutableListOf()
    var workOrdersList: List<IntermediateWorkOrder> = mutableListOf()
        set(value) {
            field = value
            workOrdersListFiltered = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediatePutAwayWorkOrdersViewHolder {
        val binding = IntermediatePutAwayWorkOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediatePutAwayWorkOrdersViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return workOrdersListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(
        holder: IntermediatePutAwayWorkOrdersViewHolder,
        position: Int
    ) {
        val workOrder = workOrdersListFiltered[position]
        with(holder) {
            binding.workOrderNumber.text = workOrder.workOrderNumber
            binding.projectTo.text = workOrder.projectNumber
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
            binding.fastPutAway.setOnClickListener {
                onItemButtonsClicked.OnFastPutAwayButtonClicked(workOrder)
            }
            binding.detailedPutAway.setOnClickListener {
                onItemButtonsClicked.OnDetailedPutAwayClicked(workOrder)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnFastPutAwayButtonClicked(workOrder: IntermediateWorkOrder)
        fun OnDetailedPutAwayClicked(workOrder: IntermediateWorkOrder)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                workOrdersListFiltered = if (charString.isEmpty()) {
                    workOrdersList
                } else {
                    val filteredList = mutableListOf<IntermediateWorkOrder>()
                    workOrdersList.filter {
                        (it.workOrderNumber?.contains(charString)!!
                                or it.projectNumber?.contains(charString)!!)

                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = workOrdersListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                workOrdersListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<IntermediateWorkOrder>
                }
                notifyDataSetChanged()
            }

        }
    }
}