package net.gbs.schneider.Ui.GetItemInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.StockSerial
import net.gbs.schneider.databinding.StockSerialItemLayoutBinding

class StockSerialsAdapter(val serials: List<StockSerial>) :
    Adapter<StockSerialsAdapter.StockSerialsViewHolder>() {
    inner class StockSerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = StockSerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockSerialsViewHolder {
        val binding =
            StockSerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockSerialsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return serials.size
    }

    override fun onBindViewHolder(holder: StockSerialsViewHolder, position: Int) {
        holder.binding.serial.text = serials[position].serial
    }
}