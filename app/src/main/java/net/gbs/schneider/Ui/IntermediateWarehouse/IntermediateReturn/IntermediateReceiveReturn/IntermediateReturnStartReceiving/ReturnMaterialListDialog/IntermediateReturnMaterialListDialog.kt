package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.IntermediateMaterialReturn
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class IntermediateReturnMaterialListDialog(
    context: Context,
    private val materialList: List<IntermediateMaterialReturn>,
    private val onMaterialItemClicked: IntermediateReturnMaterialListAdapter.OnMaterialItemClicked
) : Dialog(context) {
    private lateinit var binding: MaterialsListDialogLayoutBinding
    var isClickable = true
        set(value) {
            field = value
            adapter?.isClickable = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MaterialsListDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMaterialsRecyclerView()
    }

    val adapter: IntermediateReturnMaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = IntermediateReturnMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}