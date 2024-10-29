package net.gbs.schneider.Receiving.PutAway.SerializedMaterialListDialg

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.VendorMaterial
import net.gbs.schneider.SerialsAdapters.ShowSerialsAdapters
import net.gbs.schneider.databinding.SerializedMaterialListDialogLayoutBinding

class PutAwayVendorSerialsListDialog(private val context: Context, val material: VendorMaterial) :
    Dialog(context) {

    private lateinit var binding: SerializedMaterialListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SerializedMaterialListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillData()

    }

    private lateinit var adapter: ShowSerialsAdapters
    private fun setUpRecyclerView() {
        adapter = ShowSerialsAdapters(material.serials)
        binding.serials.adapter = adapter
    }

    private fun fillData() {
        binding.materialCode.text = material.materialCode
        binding.materialDesc.text = material.materialName
    }

    fun setSerialAsScanned(serial: Serial) {
        material.serials.find { it.serial == serial.serial }?.isReceived = serial.isReceived
//        adapter.notifyDataSetChanged()
    }

    fun setSerialAsRejected(serial: Serial) {
        material.serials.find { it.serial == serial.serial }?.isRejected = serial.isRejected
//        adapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        setUpRecyclerView()
    }
}