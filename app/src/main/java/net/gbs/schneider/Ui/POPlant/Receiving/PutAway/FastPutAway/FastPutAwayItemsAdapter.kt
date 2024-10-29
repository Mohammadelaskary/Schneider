package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.FastPutAway

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.Material
import net.gbs.schneider.R
import net.gbs.schneider.databinding.FastPutAwayItemLayoutBinding

class FastPutAwayItemsAdapter(
    private val materialList: List<Material>,
    val context: Context,
    val onFastPutAwayItemClicked: OnFastPutAwayItemClicked
) :
    Adapter<FastPutAwayItemsAdapter.FastPutAwayItemsViewHolder>() {

    inner class FastPutAwayItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FastPutAwayItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FastPutAwayItemsViewHolder {
        val binding =
            FastPutAwayItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FastPutAwayItemsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: FastPutAwayItemsViewHolder, position: Int) {
        val material = materialList[position]
        with(holder) {
            binding.materialCode.text = material.materialCode
            binding.materialDesc.text = material.materialName
            binding.serials.text = material.serialsText()
            binding.qty.text = material.remainingQtyPutAway.toString()
            if (!material.isSerializedWithOneSerial) {
                if (material.isSerialized) {
                    binding.qty.visibility = GONE
                    binding.serials.visibility = VISIBLE
                } else {
                    binding.qty.visibility = VISIBLE
                    binding.serials.visibility = GONE
                }
            } else {
                binding.serials.text = material.serials[0].serial
            }
            if (material.isSelected) {
                activateItem(holder)
            } else {
                deactivateItem(holder)
            }
            binding.root.setOnClickListener {
                onFastPutAwayItemClicked.onItemClicked(position, material.isSelected)
            }
        }
    }

    private fun activateItem(holder: FastPutAwayItemsViewHolder) {
        with(holder.binding) {
            root.setCardBackgroundColor(context.getColor(R.color.green))
            materialCode.setTextColor(context.getColor(R.color.white))
            materialDesc.setTextColor(context.getColor(R.color.white))
            serials.setTextColor(context.getColor(R.color.white))
            qty.setTextColor(context.getColor(R.color.white))
            imageView2.setImageDrawable(context.getDrawable(R.drawable.ic_barcode_icon_white))
        }
    }

    private fun deactivateItem(holder: FastPutAwayItemsViewHolder) {
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
}