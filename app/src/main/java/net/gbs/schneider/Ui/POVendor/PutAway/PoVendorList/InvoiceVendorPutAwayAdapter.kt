package net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.databinding.InvoiceVendorItemPutAwayLayoutBinding

class InvoiceVendorPutAwayAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    Adapter<InvoiceVendorPutAwayAdapter.InvoiceViewHolder>(), Filterable {
    var invoiceListFiltered: List<InvoiceVendor> = mutableListOf()
    var invoicesList: List<InvoiceVendor> = mutableListOf()
        set(value) {
            field = value
            invoiceListFiltered = value
            notifyDataSetChanged()
        }

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InvoiceVendorItemPutAwayLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = InvoiceVendorItemPutAwayLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InvoiceViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return invoiceListFiltered.size
    }

    var currentSelectedPosition = -1
    var lastSelectedPosition = -1
    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoiceListFiltered[position]
        with(holder) {
            binding.projectNumber.text = invoice.projectNumber
            binding.poNumber.text = invoice.poNumber
            binding.supplyPlant.text = invoice.supplyVendor
            itemView.setOnClickListener {
                lastSelectedPosition = currentSelectedPosition
                currentSelectedPosition = adapterPosition
                notifyItemChanged(position)
                notifyItemChanged(lastSelectedPosition)
            }
            if (currentSelectedPosition == position) {
                binding.buttonsGroup.visibility = VISIBLE
            } else {
                binding.buttonsGroup.visibility = GONE
            }
            binding.startPutAway.setOnClickListener {
                onItemButtonsClicked.OnStartPutAwayButtonClicked(invoice)
            }
            binding.details.setOnClickListener {
                onItemButtonsClicked.OnDetailsButtonClicked(invoice)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartPutAwayButtonClicked(invoice: InvoiceVendor)
        fun OnDetailsButtonClicked(invoice: InvoiceVendor)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
                    invoiceListFiltered = invoicesList
                } else {
                    val filteredList = mutableListOf<InvoiceVendor>()
                    invoicesList.filter {
                        (it.projectNumber!!.contains(constraint!!)) or
                                (it.poNumber!!.contains(constraint))
                    }.forEach { filteredList.add(it) }
                    invoiceListFiltered = filteredList
                }
                return FilterResults().apply { values = invoiceListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                invoiceListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<InvoiceVendor>
                }
                notifyDataSetChanged()
            }

        }
    }
}