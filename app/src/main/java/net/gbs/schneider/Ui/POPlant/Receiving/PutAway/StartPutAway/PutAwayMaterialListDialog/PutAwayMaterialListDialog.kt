package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.StartPutAway.PutAwayMaterialListDialogPutAway.IntermediatePutAwayMaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog.PutAwayMaterialListAdapter
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class PutAwayMaterialListDialog(
    context: Context,
    private val materialList: List<Material>,
    private val onMaterialItemClicked: PutAwayMaterialListAdapter.OnMaterialItemClicked
) : Dialog(context) {
    private lateinit var binding: MaterialsListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaterialsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    val adapter: PutAwayMaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = PutAwayMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}