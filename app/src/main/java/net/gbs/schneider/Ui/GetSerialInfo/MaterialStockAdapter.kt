package net.gbs.schneider.Ui.GetSerialInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import net.gbs.schneider.Model.Stock
import net.gbs.schneider.databinding.MaterialStockItemBinding

class MaterialStockAdapter(val context: Context) :
    Adapter<MaterialStockAdapter.MaterialStockViewHolder>() {
    var stockList: List<Stock> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class MaterialStockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = MaterialStockItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialStockViewHolder {
        val binding =
            MaterialStockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialStockViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    override fun onBindViewHolder(holder: MaterialStockViewHolder, position: Int) {
        val stock = stockList[position]
        with(holder.binding) {
            materialCode.text = stock.materialCode
            materialDesc.text = stock.materialName
            stockQty.text = stock.stockQty.toString()
            val locationText =
                "${stock.warehouseName} -> ${stock.storageLocationName} -> ${stock.storageSectionCode} -> ${stock.storageBinCode}"
            location.text = locationText
            serialsListButton.setOnClickListener {
                val serialsDialog = InfoSerialsListDialog(stock.stockSerials, context)
                serialsDialog.show()
            }
            if (stock.isSerialized!!) {
                serialsListButton.visibility = VISIBLE
            } else {
                serialsListButton.visibility = GONE
            }
        }

    }
}