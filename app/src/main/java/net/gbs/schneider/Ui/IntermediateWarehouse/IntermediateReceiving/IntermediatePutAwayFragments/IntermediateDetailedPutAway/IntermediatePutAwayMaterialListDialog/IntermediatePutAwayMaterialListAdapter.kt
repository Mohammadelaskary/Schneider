package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway.IntermediatePutAwayMaterialListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.databinding.IntermediatePutAwayMaterialItemBinding

class IntermediatePutAwayMaterialListAdapter(
    private val materialList: List<IntermediateMaterial>,
    val onMaterialItemClicked: OnMaterialItemClicked
) : Adapter<IntermediatePutAwayMaterialListAdapter.MaterialViewHolder>() {

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = IntermediatePutAwayMaterialItemBinding.bind(itemView)
    }

    var isClickable = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = IntermediatePutAwayMaterialItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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
            binding.putAwayPerReceivedQty.text = "${materialItem.remainingQtyPutAway}"

            itemView.setOnClickListener {
                if (isClickable)
                    onMaterialItemClicked.onMaterialItemClicked(materialItem)
            }
        }
    }

    interface OnMaterialItemClicked {
        fun onMaterialItemClicked(material: IntermediateMaterial)
    }
}