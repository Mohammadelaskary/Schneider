package net.gbs.schneider.SerialsAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.databinding.SerialItemLayoutBinding
import net.gbs.schneider.databinding.ShowSerialItemLayoutBinding

class ShowSerialsAdapters (val serialsList:List<Serial>) : RecyclerView.Adapter<ShowSerialsAdapters.ShowSerialsViewHolder>() {
    inner class ShowSerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ShowSerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowSerialsViewHolder {
        val binding =
            ShowSerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowSerialsViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ShowSerialsViewHolder, position: Int) {
        holder.binding.serial.text = serialsList[position].serial
    }

    override fun getItemCount(): Int {
        return serialsList.size
    }
}