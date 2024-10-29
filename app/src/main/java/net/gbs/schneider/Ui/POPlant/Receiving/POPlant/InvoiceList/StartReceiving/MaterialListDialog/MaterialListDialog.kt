package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.Material
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class MaterialListDialog(
    context: Context,
    private val materialList: List<Material>,
    private val onMaterialItemClicked: MaterialListAdapter.OnMaterialItemClicked
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
        val adapter = MaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}