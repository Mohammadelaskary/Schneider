package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediateReturnStartPutAway.IntermediatePutAwayReturnMaterialListDialog

import Intermediate.IntermediatePutAwayReturnMaterialListAdapter
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.IntermediateMaterialReturn
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class IntermediatePutAwayReturnMaterialListDialog(
    context: Context,
    private val materialList: List<IntermediateMaterialReturn>,
    private val onMaterialItemClicked: IntermediatePutAwayReturnMaterialListAdapter.OnMaterialItemClicked
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

    val adapter: IntermediatePutAwayReturnMaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter =
            IntermediatePutAwayReturnMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}