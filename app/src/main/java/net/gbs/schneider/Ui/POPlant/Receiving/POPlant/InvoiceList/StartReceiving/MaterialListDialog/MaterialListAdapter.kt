package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.Material
import net.gbs.schneider.databinding.MaterialItemBinding

class MaterialListAdapter(
    private val materialList: List<Material>,
    val onMaterialItemClicked: OnMaterialItemClicked
) : Adapter<MaterialListAdapter.MaterialViewHolder>() {

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MaterialItemBinding.bind(itemView)
    }

    var isClickable = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding =
            MaterialItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val materialItem = materialList[position]
        with(holder) {
            binding.materialCode.text = materialItem.materialCode
            binding.materialDesc.text = materialItem.materialName
            binding.shippedPerReceivedQty.text =
                "${materialItem.receivedQty} / ${materialItem.shippedQuantity}"
            itemView.setOnClickListener {
                if (isClickable)
                    onMaterialItemClicked.onMaterialItemClicked(materialItem)
            }
        }
    }

    interface OnMaterialItemClicked {
        fun onMaterialItemClicked(material: Material)
    }
}