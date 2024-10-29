package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.VendorMaterial
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.MaterialListAdapter
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class MaterialVendorListDialog(
    context: Context,
    private val materialList: List<VendorMaterial>,
    private val onMaterialItemClicked: MaterialVendorListAdapter.OnMaterialItemClicked
) : Dialog(context) {
    private lateinit var binding: MaterialsListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaterialsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    val adapter: MaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = MaterialVendorListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}