package net.gbs.schneider.Ui.POVendor.PoVendorDetailsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.databinding.InvoiceVendorDetailsItemLayoutBinding

class InvoiceVendorDetailsAdapter(val invoice: InvoiceVendor) :
    RecyclerView.Adapter<InvoiceVendorDetailsAdapter.InvoiceDetailsViewHolder>() {
    inner class InvoiceDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = InvoiceVendorDetailsItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceDetailsViewHolder {
        val binding = InvoiceVendorDetailsItemLayoutBinding.inflate(
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
            projectNumber.text = invoice.projectNumber
            poNumber.text = invoice.poNumber
            supplyPlant.text = invoice.supplyVendor
            materialCode.text = invoice.materials[position].materialCode
            materialDesc.text = invoice.materials[position].materialName
            shippedQty.text = invoice.materials[position].shippedQuantity.toString()
        }
    }
}