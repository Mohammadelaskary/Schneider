package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList.ChangeSerialsInvoiceAdapter
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding


class ChangeSerialsMaterialListDialog(
    context: Context,
    private val materialList: List<Material>,
    private val onMaterialItemClicked: ChangeSerialsMaterialListAdapter.OnMaterialItemClicked
) : Dialog(context) {
    private lateinit var binding: MaterialsListDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaterialsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    val adapter: ChangeSerialsInvoiceAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = ChangeSerialsMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}