package net.gbs.schneider.Return.Receive.InvoiceList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.databinding.PutAwayReturnInvoiceItemLayoutBinding

class PutAwayReturnInvoiceListAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    RecyclerView.Adapter<PutAwayReturnInvoiceListAdapter.PutAwayReturnInvoiceListViewHolder>(),
    Filterable {
    var invoiceListFiltered: List<WorkOrderReturn> = listOf()
    var invoicesList: List<WorkOrderReturn> = listOf()
        set(value) {
            field = value
            invoiceListFiltered = value
            notifyDataSetChanged()
        }

    inner class PutAwayReturnInvoiceListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = PutAwayReturnInvoiceItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PutAwayReturnInvoiceListViewHolder {
        val binding = PutAwayReturnInvoiceItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PutAwayReturnInvoiceListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return invoiceListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(holder: PutAwayReturnInvoiceListViewHolder, position: Int) {
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
            binding.startPutAway.setOnClickListener {
                onItemButtonsClicked.OnStartReturnButtonClicked(invoice)
            }
            binding.details.setOnClickListener {
                onItemButtonsClicked.OnDetailsButtonClicked(invoice)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartReturnButtonClicked(invoice: WorkOrderReturn)
        fun OnDetailsButtonClicked(invoice: WorkOrderReturn)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
                    invoiceListFiltered = invoicesList
                } else {
                    val filteredList = mutableListOf<WorkOrderReturn>()
                    invoicesList.filter {
                        (it.workOrderNumber.contains(constraint!!)) or
                                (it.projectNumber.contains(constraint))
                    }.forEach { filteredList.add(it) }
                    invoiceListFiltered = filteredList
                }
                return FilterResults().apply { values = invoiceListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                invoiceListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<WorkOrderReturn>
                }
                notifyDataSetChanged()
            }

        }
    }
}