package net.gbs.schneider.SerialsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.databinding.ScannedSerialLayoutBinding

class ScannedSerialsAdapter(private val onSerialButtonsClicked: OnSerialButtonsClicked, private val scannedSerials :MutableList<Serial>) : Adapter<ScannedSerialsAdapter.ScannedSerialsViewHolder>() {

    inner class ScannedSerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ScannedSerialLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedSerialsViewHolder {
        val binding = ScannedSerialLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ScannedSerialsViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return scannedSerials.size
    }

    override fun onBindViewHolder(holder: ScannedSerialsViewHolder, position: Int) {
        val serial = scannedSerials[position]
        with(holder){
            binding.serial.text = serial.serial
            binding.rejected.isChecked = serial.isRejected
            binding.rejected.setOnCheckedChangeListener { _, isRejected ->
                onSerialButtonsClicked.onSerialRejected(position, isRejected)
            }
            binding.remove.setOnClickListener {
                onSerialButtonsClicked.onRemoveButtonClicked(position)
            }
        }

    }

    interface OnSerialButtonsClicked {
        fun onSerialRejected (position: Int,isRejected:Boolean)
        fun onRemoveButtonClicked (position: Int)
    }
}