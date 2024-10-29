package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.databinding.ReturnMaterialItemBinding

class ReturnMaterialListAdapter(
    private val materialList: List<MaterialReturn>,
    val onMaterialItemClicked: OnMaterialItemClicked
) : Adapter<ReturnMaterialListAdapter.ReturnMaterialViewHolder>() {

    inner class ReturnMaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ReturnMaterialItemBinding.bind(itemView)
    }

    var isClickable = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnMaterialViewHolder {
        val binding =
            ReturnMaterialItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReturnMaterialViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return materialList.size
    }

    override fun onBindViewHolder(holder: ReturnMaterialViewHolder, position: Int) {
        val materialItem = materialList[position]
        with(holder) {
            binding.materialCode.text = materialItem.materialCode
            binding.materialDesc.text = materialItem.materialName
            binding.rceivedPerReturn.text =
                "${materialItem.receivedQuantity} / ${materialItem.returnedQuantity}"
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