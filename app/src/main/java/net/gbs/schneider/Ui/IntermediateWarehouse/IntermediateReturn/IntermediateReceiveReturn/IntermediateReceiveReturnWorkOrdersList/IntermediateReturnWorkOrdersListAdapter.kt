package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReceiveReturn.IntermediateReceiveReturnWorkOrdersList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.databinding.ReturnInvoiceItemLayoutBinding

class IntermediateReturnWorkOrdersListAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    RecyclerView.Adapter<IntermediateReturnWorkOrdersListAdapter.IntermediateReturnWorkOrdersListViewHolder>(),
    Filterable {
    var invoiceListFiltered: List<IntermediateWorkOrderReturn> = listOf()
    var invoicesList: List<IntermediateWorkOrderReturn> = listOf()
        set(value) {
            field = value
            invoiceListFiltered = value
            notifyDataSetChanged()
        }

    inner class IntermediateReturnWorkOrdersListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ReturnInvoiceItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateReturnWorkOrdersListViewHolder {
        val binding = ReturnInvoiceItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediateReturnWorkOrdersListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return invoiceListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(
        holder: IntermediateReturnWorkOrdersListViewHolder,
        position: Int
    ) {
        val invoice = invoiceListFiltered[position]
        with(holder) {
            binding.projectNumber.text = invoice.projectNumber.toString()
            binding.workOrderNumber.text = invoice.workOrderNumber.toString()
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
            binding.startReturn.setOnClickListener {
                onItemButtonsClicked.OnStartReturnButtonClicked(invoice)
            }
            binding.details.setOnClickListener {
                onItemButtonsClicked.OnDetailsButtonClicked(invoice)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartReturnButtonClicked(workOrderReturn: IntermediateWorkOrderReturn)
        fun OnDetailsButtonClicked(workOrderReturn: IntermediateWorkOrderReturn)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                invoiceListFiltered = if (charString.isEmpty()) {
                    invoicesList
                } else {
                    val filteredList = mutableListOf<IntermediateWorkOrderReturn>()
                    invoicesList.filter {
                        (it.workOrderNumber.contains(constraint!!) or (it.projectNumber.contains(
                            constraint
                        )))
                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = invoiceListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                invoiceListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<IntermediateWorkOrderReturn>
                }
                notifyDataSetChanged()
            }

        }
    }
}