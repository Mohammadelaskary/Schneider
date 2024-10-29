package net.gbs.schneider.SerialsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.databinding.SerialItemLayoutBinding

class SerialsWithRemoveButtonAdapter(val serialsList:List<Serial>, val onRemoveSerialButtonClicked: OnRemoveSerialButtonClicked) : RecyclerView.Adapter<SerialsWithRemoveButtonAdapter.SerialsViewHolder>() {
    inner class SerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = SerialItemLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerialsViewHolder {
        val binding = SerialItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SerialsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return serialsList.size
    }

    override fun onBindViewHolder(holder: SerialsViewHolder, position: Int) {
        val serial = serialsList[position]
        holder.binding.serial.text = serial.serial
        holder.binding.removeSerial.setOnClickListener {
            onRemoveSerialButtonClicked.OnRemovedSerial(position)
        }
    }
    interface OnRemoveSerialButtonClicked {
        fun OnRemovedSerial(position: Int)
    }
}