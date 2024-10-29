package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateFastPutAway

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.R
import net.gbs.schneider.databinding.FastPutAwayItemLayoutBinding
import net.gbs.schneider.databinding.IntermediateFastPutAwayItemLayoutBinding

class IntermediateFastPutAwayItemAdapter(
    private val materialList: List<IntermediateMaterial>,
    val context: Context,
    val onFastPutAwayItemClicked: OnFastPutAwayItemClicked
) :
    RecyclerView.Adapter<IntermediateFastPutAwayItemAdapter.IntermediateFastPutAwayItemsViewHolder>() {

    inner class IntermediateFastPutAwayItemsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = FastPutAwayItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateFastPutAwayItemsViewHolder {
        val binding =
            IntermediateFastPutAwayItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return IntermediateFastPutAwayItemsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: IntermediateFastPutAwayItemsViewHolder, position: Int) {
        val material = materialList[position]
        with(holder.binding) {
            materialCode.text = material.materialCode
            materialDesc.text = material.materialName
            qty.text = material.remainingQtyPutAway.toString()
            if (material.isSerializedWithOneSerial) {
                serials.visibility = View.VISIBLE
                serials.text = material.serials[0].serial
                qty.visibility = View.VISIBLE
            } else {
                if (material.isSerialized) {
                    serials.visibility = View.VISIBLE
                    serials.text = HtmlCompat.fromHtml(
                        getSerialsText(material),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    qty.visibility = View.GONE
                } else {
                    serials.visibility = View.GONE
                    val receivedPerIssued = material.remainingQtyPutAway.toString()
                    qty.text = receivedPerIssued
                    qty.visibility = View.VISIBLE
                }
            }
            if (material.isSelected) {
                activateItem(holder)
            } else {
                deactivateItem(holder)
            }
            root.setOnClickListener {
                onFastPutAwayItemClicked.onItemClicked(position, material.isSelected)
            }
        }
    }

    private fun activateItem(holder: IntermediateFastPutAwayItemsViewHolder) {
        with(holder.binding) {
            root.setCardBackgroundColor(context.getColor(R.color.green))
            materialCode.setTextColor(context.getColor(R.color.white))
            materialDesc.setTextColor(context.getColor(R.color.white))
            serials.setTextColor(context.getColor(R.color.white))
            qty.setTextColor(context.getColor(R.color.white))
            imageView2.setImageDrawable(context.getDrawable(R.drawable.ic_barcode_icon_white))
        }
    }

    private fun deactivateItem(holder: IntermediateFastPutAwayItemsViewHolder) {
        with(holder.binding) {
            root.setCardBackgroundColor(context.getColor(R.color.white))
            materialCode.setTextColor(context.getColor(R.color.grey))
            materialDesc.setTextColor(context.getColor(R.color.grey))
            serials.setTextColor(context.getColor(R.color.grey))
            qty.setTextColor(context.getColor(R.color.grey))
            imageView2.setImageDrawable(context.getDrawable(R.drawable.ic_barcode_icon))
        }
    }

    interface OnFastPutAwayItemClicked {
        fun onItemClicked(position: Int, isSelected: Boolean)
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