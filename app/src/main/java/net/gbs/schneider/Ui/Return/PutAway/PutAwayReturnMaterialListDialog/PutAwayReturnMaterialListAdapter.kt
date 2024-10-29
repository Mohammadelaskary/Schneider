package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.databinding.PutAwayReturnMaterialItemBinding

class PutAwayReturnMaterialListAdapter(
    private val materialList: List<MaterialReturn>,
    val onMaterialItemClicked: OnMaterialItemClicked
) : Adapter<PutAwayReturnMaterialListAdapter.PutAwayReturnMaterialViewHolder>() {

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
        fun onMaterialItemClicked(material: MaterialReturn)
    }
}