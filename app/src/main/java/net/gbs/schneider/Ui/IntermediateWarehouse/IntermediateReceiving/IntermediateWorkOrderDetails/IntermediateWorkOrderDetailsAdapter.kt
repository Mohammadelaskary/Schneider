package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateWorkOrderDetails

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.databinding.IntermediateWorkOrderDetailsItemBinding

class IntermediateWorkOrderDetailsAdapter(val intermediateMaterialsList: List<IntermediateMaterial>) :
    Adapter<IntermediateWorkOrderDetailsAdapter.IntermediateWorkOrderDetailsViewHolder>() {
    inner class IntermediateWorkOrderDetailsViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = IntermediateWorkOrderDetailsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateWorkOrderDetailsViewHolder {
        val binding = IntermediateWorkOrderDetailsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediateWorkOrderDetailsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return intermediateMaterialsList.size
    }

    override fun onBindViewHolder(holder: IntermediateWorkOrderDetailsViewHolder, position: Int) {
        val material = intermediateMaterialsList[position]
        with(holder.binding) {
            materialCode.text = material.materialCode
            materialDesc.text = material.materialName

            if (material.isSerializedWithOneSerial) {
                serials.visibility = VISIBLE
                serials.text = material.serials[0].serial
                qty.visibility = VISIBLE
                val receivedPerIssued = "${material.receivedQuantity} / ${material.issuedQuantity}"
                qty.text = receivedPerIssued
            } else {
                if (material.isSerialized) {
                    serials.visibility = VISIBLE
                    serials.text = HtmlCompat.fromHtml(
                        getSerialsText(material),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    qty.visibility = GONE
                } else {
                    serials.visibility = GONE
                    val receivedPerIssued =
                        "${material.receivedQuantity} / ${material.issuedQuantity}"
                    qty.text = receivedPerIssued
                    qty.visibility = VISIBLE
                }
            }
        }
    }

    private fun getSerialsText(material: IntermediateMaterial): String {
        var serialsText = ""
        material.serials.forEachIndexed { index, serial ->
            serialsText += if (index == material.serials.size - 1) {
                if (serial.isReceived)
                    "<b>${serial.serial}</b>"
                else
                    serial.serial
            } else {
                if (serial.isReceived)
                    "<b>${serial.serial}</b>, "
                else
                    "${serial.serial}, "
            }

        }
        return serialsText
    }
}