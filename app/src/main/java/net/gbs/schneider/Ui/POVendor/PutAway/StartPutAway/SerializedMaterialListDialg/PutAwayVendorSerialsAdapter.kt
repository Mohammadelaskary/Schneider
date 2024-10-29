package net.gbs.schneider.Receiving.PutAway.SerializedMaterialListDialg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.R
import net.gbs.schneider.databinding.SerialItemLayoutBinding

class PutAwayVendorSerialsAdapter(val serialsList: List<Serial>, val context: Context) :
    RecyclerView.Adapter<PutAwayVendorSerialsAdapter.SerialsViewHolder>() {
    inner class SerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerialsViewHolder {
        val binding =
            SerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SerialsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return serialsList.size
    }

    override fun onBindViewHolder(holder: SerialsViewHolder, position: Int) {
        val serial = serialsList[position]
        holder.binding.serial.text = serial.serial
        if (serial.isPutaway) {
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