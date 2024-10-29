package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.databinding.SerialItemLayoutBinding

class IntermediateSerialsAdapter_Return(val serialsList: List<Serial>, val context: Context) :
    RecyclerView.Adapter<IntermediateSerialsAdapter_Return.SerialsViewHolder_Return>() {
    inner class SerialsViewHolder_Return(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerialsViewHolder_Return {
        val binding =
            SerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SerialsViewHolder_Return(binding.root)
    }

    override fun getItemCount(): Int {
        return serialsList.size
    }

    override fun onBindViewHolder(holder: SerialsViewHolder_Return, position: Int) {
        val serial = serialsList[position]
        holder.binding.serial.text = serial.serial
        if (serial.isReceived) {
            holder.binding.serial.setTextColor(context.getColor(R.color.white))
            holder.binding.root.setBackgroundColor(context.getColor(R.color.logo_green))
            if (serial.isRejected)
                holder.binding.root.setBackgroundColor(context.getColor(android.R.color.holo_red_dark))
        } else {
            holder.binding.serial.setTextColor(context.getColor(R.color.grey))
            holder.binding.root.setBackgroundColor(context.getColor(R.color.white))
        }
    }
}