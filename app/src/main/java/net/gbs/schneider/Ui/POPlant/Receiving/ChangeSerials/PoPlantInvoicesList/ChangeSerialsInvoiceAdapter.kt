package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.StoragePermission
import net.gbs.schneider.databinding.InvoiceItemChangeSerialsLayoutBinding

class ChangeSerialsInvoiceAdapter(private val onItemButtonsClicked: OnItemButtonsClicked) :
    Adapter<ChangeSerialsInvoiceAdapter.InvoiceViewHolder>(), Filterable {
    var invoiceListFiltered: List<Invoice> = mutableListOf()
    var invoicesList: List<Invoice> = mutableListOf()
        set(value) {
            field = value
            invoiceListFiltered = value
            notifyDataSetChanged()
        }

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InvoiceItemChangeSerialsLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = InvoiceItemChangeSerialsLayoutBinding.inflate(
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
            binding.invoiceNumber.text = invoice.invoiceNumber
            binding.projectNumber.text = invoice.projectNumber
            if (invoice.storagePermissions.isEmpty())
                invoice.storagePermissions.add(StoragePermission())
            binding.mrn.text = invoice.storagePermissions[0].mrn
            binding.billOfLadingNo.text = invoice.storagePermissions[0].billOfLadingNo
            binding.declarationNo.text = invoice.storagePermissions[0].declarationNo
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
            binding.changeSerials.setOnClickListener {
                onItemButtonsClicked.OnStartSerialsButtonClicked(invoice)
            }
            binding.changeQty.setOnClickListener {
                onItemButtonsClicked.OnStartQtyButtonClicked(invoice)
            }
        }
    }

    interface OnItemButtonsClicked {
        fun OnStartSerialsButtonClicked(invoice: Invoice)
        fun OnStartQtyButtonClicked(invoice: Invoice)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
                    invoiceListFiltered = invoicesList
                } else {
                    val filteredList = mutableListOf<Invoice>()
                    invoicesList.filter {
                        (it.invoiceNumber.contains(constraint!!)) or
                                (it.projectNumber.contains(constraint)) or
                                (it.salesOrderNumber.contains(constraint)) or
                                (it.poNumber.contains(constraint))
                    }.forEach { filteredList.add(it) }
                    invoiceListFiltered = filteredList
                }
                return FilterResults().apply { values = invoiceListFiltered }
            }

            override fun publishResults(p0: CharSequence?, results: FilterResults?) {
                invoiceListFiltered = if (results?.values == null) {
                    mutableListOf()
                } else {
                    results.values as List<Invoice>
                }
                notifyDataSetChanged()
            }

        }
    }
}