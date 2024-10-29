package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.InvoiceDetailsList

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.databinding.InvoiceDetailsItemLayoutBinding

class InvoiceDetailsAdapter(
    val invoice: Invoice,
    val onShowSerialsButtonClicked: OnShowSerialsButtonClicked
) : RecyclerView.Adapter<InvoiceDetailsAdapter.InvoiceDetailsViewHolder>() {
    inner class InvoiceDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InvoiceDetailsItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceDetailsViewHolder {
        val binding = InvoiceDetailsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InvoiceDetailsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return invoice.materials.size
    }

    override fun onBindViewHolder(holder: InvoiceDetailsViewHolder, position: Int) {
        with(holder.binding) {
            invoiceNumber.text = invoice.invoiceNumber
            projectNumber.text = invoice.projectNumber
            poNumber.text = invoice.poNumber
            salesOrderNum.text = invoice.salesOrderNumber
            boxNumber.text = invoice.materials[position].boxNumber
            lineNumber.text = invoice.materials[position].lineNumber.toString()
            materialCode.text = invoice.materials[position].materialCode
            materialDesc.text = invoice.materials[position].materialName
            shippedQty.text = invoice.materials[position].shippedQuantity.toString()
            receivedQty.text = invoice.materials[position].receivedQty.toString()
            if (invoice.materials[position].isSerialized) {
                showSerials.visibility = VISIBLE
            } else {
                showSerials.visibility = GONE
            }
            showSerials.setOnClickListener {
                onShowSerialsButtonClicked.onShowSerialsButtonClicked(invoice.materials[position])
            }
        }
    }

    interface OnShowSerialsButtonClicked {
        fun onShowSerialsButtonClicked(material: Material)
    }
}