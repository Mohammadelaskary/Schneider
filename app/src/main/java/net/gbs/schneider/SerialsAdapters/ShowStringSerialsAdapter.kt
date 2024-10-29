package net.gbs.schneider.SerialsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.ReturnSerial
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.databinding.ShowSerialItemLayoutBinding

class ShowStringSerialsAdapter (val serialsList:List<ReturnSerial>) : RecyclerView.Adapter<ShowStringSerialsAdapter.ShowStringSerialsViewHolder>() {
    inner class ShowStringSerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ShowSerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowStringSerialsViewHolder {
        val binding =
            ShowSerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowStringSerialsViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ShowStringSerialsViewHolder, position: Int) {
        holder.binding.serial.text = serialsList[position].serial
    }

    override fun getItemCount(): Int {
        return serialsList.size
    }
}