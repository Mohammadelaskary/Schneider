package Intermediate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.IntermediateMaterialReturn
import net.gbs.schneider.databinding.PutAwayReturnMaterialItemBinding

class IntermediatePutAwayReturnMaterialListAdapter(
    private val materialList: List<IntermediateMaterialReturn>,
    val onMaterialItemClicked: OnMaterialItemClicked
) : Adapter<IntermediatePutAwayReturnMaterialListAdapter.PutAwayReturnMaterialViewHolder>() {

    inner class PutAwayReturnMaterialViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = PutAwayReturnMaterialItemBinding.bind(itemView)
    }

    var isClickable = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PutAwayReturnMaterialViewHolder {
        val binding = PutAwayReturnMaterialItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PutAwayReturnMaterialViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: PutAwayReturnMaterialViewHolder, position: Int) {
        val materialItem = materialList[position]
        with(holder) {
            binding.materialCode.text = materialItem.materialCode
            binding.materialDesc.text = materialItem.materialName
            binding.putAwayPerReceived.text =
                "${materialItem.putawayQuantity} / ${materialItem.receivedQuantity}"
            itemView.setOnClickListener {
                if (isClickable)
                    onMaterialItemClicked.onMaterialItemClicked(materialItem)
            }
        }
    }

    interface OnMaterialItemClicked {
        fun onMaterialItemClicked(material: IntermediateMaterialReturn)
    }
}