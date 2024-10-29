package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway.IntermediatePutAwayMaterialListDialog.IntermediatePutAwayMaterialListAdapter
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class IntermediatePutAwayMaterialListDialog(
    context: Context,
    private val materialList: List<IntermediateMaterial>,
    private val onMaterialItemClicked: IntermediatePutAwayMaterialListAdapter.OnMaterialItemClicked
) : Dialog(context) {
    private lateinit var binding: MaterialsListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaterialsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    val adapter: IntermediatePutAwayMaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = IntermediatePutAwayMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}