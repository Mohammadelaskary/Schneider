package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.StartChangingSerials

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import net.gbs.schneider.Model.SerialStatus
import net.gbs.schneider.Model.SerialWithStatus
import net.gbs.schneider.R
import net.gbs.schneider.databinding.ChangeSerialItemBinding

class ChangeSerialsAdapter(
    val serials: List<SerialWithStatus>,
    val onRemoveCheckBoxClicked: OnRemoveCheckBoxClicked
) : Adapter<ChangeSerialsAdapter.ChangeSerialsViewHolder>() {
    inner class ChangeSerialsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ChangeSerialItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeSerialsViewHolder {
        val changeSerialItemBinding =
            ChangeSerialItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChangeSerialsViewHolder(changeSerialItemBinding.root)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${serials.size}")
        return serials.size
    }

    override fun onBindViewHolder(holder: ChangeSerialsViewHolder, position: Int) {
        val serial = serials[position]
        with(holder) {
            binding.serial.text = serial.serial
            if (serial.serialStatus == SerialStatus.REMOVED) {
                binding.removeUndo.setIconResource(R.drawable.ic_undo)
            } else {
                binding.removeUndo.setIconResource(R.drawable.ic_delete)
            }
            when (serial.serialStatus) {
                SerialStatus.NEW -> binding.background.setBackgroundResource(R.drawable.new_serial_background)
                SerialStatus.REMOVED -> binding.background.setBackgroundResource(R.drawable.removed_serial_background)
                SerialStatus.ORIGINAL -> binding.background.setBackgroundResource(R.drawable.original_serial_background)
            }
            binding.removeUndo.setOnClickListener {
                onRemoveCheckBoxClicked.onRemoveClicked(
                    position,
                    serial.serialStatus
                )
            }
        }
    }

    interface OnRemoveCheckBoxClicked {
        fun onRemoveClicked(position: Int, isChecked: SerialStatus)
    }
}