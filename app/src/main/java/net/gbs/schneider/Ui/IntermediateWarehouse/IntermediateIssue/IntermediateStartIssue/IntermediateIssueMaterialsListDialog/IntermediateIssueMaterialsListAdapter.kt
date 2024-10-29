package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateIssue.IntermediateStartIssue.IntermediateIssueMaterialsListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.IntermediateIssueWorkOrderDetails
import net.gbs.schneider.databinding.IntermediateIssueMaterialsItemBinding

class IntermediateIssueMaterialsListAdapter(
    private val materialsList: MutableList<IntermediateIssueWorkOrderDetails>,
    val onIntermediateMaterialsItemClicked: OnIntermediateMaterialsItemClicked
) : Adapter<IntermediateIssueMaterialsListAdapter.IntermediateMaterialsViewHolder>() {

    inner class IntermediateMaterialsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = IntermediateIssueMaterialsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IntermediateMaterialsViewHolder {
        val binding = IntermediateIssueMaterialsItemBinding.inflate(
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
            binding.issuedPerTotal.text =
                "${material.issuedQuantity} / ${material.workOrderDetailQuantity}"
            itemView.setOnClickListener {
                onIntermediateMaterialsItemClicked.onIntermediateMaterialsItemClicked(material)
            }
        }
    }

    interface OnIntermediateMaterialsItemClicked {
        fun onIntermediateMaterialsItemClicked(material: IntermediateIssueWorkOrderDetails)
    }
}