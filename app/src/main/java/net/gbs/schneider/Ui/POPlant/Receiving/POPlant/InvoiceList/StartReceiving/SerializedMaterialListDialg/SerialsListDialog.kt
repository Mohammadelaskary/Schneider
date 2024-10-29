package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.SerializedMaterialListDialg

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.SerialsAdapters.ShowSerialsWithStatusAdapter
import net.gbs.schneider.databinding.SerializedMaterialListDialogLayoutBinding

class SerialsListDialog(private val context: Context, val material: Material) : Dialog(context) {

    private lateinit var binding: SerializedMaterialListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerializedMaterialListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillData()

    }

    private lateinit var adapter: ShowSerialsWithStatusAdapter
    private fun setUpRecyclerView() {
        val serials = if (material.isSerializedWithOneSerial)
            listOf(
                material.serials[0]
            )
        else
            material.serials
        adapter = ShowSerialsWithStatusAdapter(serials, context)
        binding.serials.adapter = adapter
    }

    private fun fillData() {
        binding.materialCode.text = material.materialCode
        binding.materialDesc.text = material.materialName
    }

    fun setSerialAsScanned(serial: Serial) {
        material.serials.find { it.serial == serial.serial }?.isReceived = serial.isReceived
        material.serials.find { it.serial == serial.serial }?.isRejected = serial.isRejected
//        adapter.notifyDataSetChanged()
    }

    fun setSerialAsRejected(serial: Serial) {
        material.serials.find { it.serial == serial.serial }?.isRejected = serial.isRejected
//        adapter.notifyDataSetChanged()
    }

    fun setSerialAsNotRejected(serial: Serial) {
        material.serials.find { it.serial == serial.serial }?.isRejected = false
//        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        setUpRecyclerView()
    }

}