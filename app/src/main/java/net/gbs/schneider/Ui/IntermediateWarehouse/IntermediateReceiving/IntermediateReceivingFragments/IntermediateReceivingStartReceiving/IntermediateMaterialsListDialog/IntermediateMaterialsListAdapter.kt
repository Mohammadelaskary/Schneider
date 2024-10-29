package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving.IntermediateMaterialsListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.databinding.IntermediateMaterialsItemBinding

class IntermediateMaterialsListAdapter(
    private val materialsList: MutableList<IntermediateMaterial>,
    val onIntermediateMaterialsItemClicked: OnIntermediateMaterialsItemClicked
) : Adapter<IntermediateMaterialsListAdapter.IntermediateMaterialsViewHolder>() {

    inner class IntermediateMaterialsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = IntermediateMaterialsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateMaterialsViewHolder {
        val binding = IntermediateMaterialsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IntermediateMaterialsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialsList.size
    }

    override fun onBindViewHolder(holder: IntermediateMaterialsViewHolder, position: Int) {
        val material = materialsList[position]
        with(holder) {
            binding.materialCode.text = material.materialCode
            binding.materialDesc.text = material.materialName
//            binding.projectDesc.text = workOrderDetailsItem.projectDesc
//            binding.location.text = workOrderDetailsItem.location.toString()
            binding.receivedQtyPerIssuedQty.text =
                "${material.receivedQuantity} / ${material.issuedQuantity}"
            itemView.setOnClickListener {
                onIntermediateMaterialsItemClicked.onIntermediateMaterialsItemClicked(material)
            }
        }
    }

    interface OnIntermediateMaterialsItemClicked {
        fun onIntermediateMaterialsItemClicked(material: IntermediateMaterial)
    }
}