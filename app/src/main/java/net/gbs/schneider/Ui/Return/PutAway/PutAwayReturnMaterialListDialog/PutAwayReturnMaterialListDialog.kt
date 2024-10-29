package net.gbs.schneider.Receiving.POPlant.InvoiceList.StartReceiving.MaterialListDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import net.gbs.schneider.Model.MaterialReturn
import net.gbs.schneider.databinding.MaterialsListDialogLayoutBinding

class PutAwayReturnMaterialListDialog(
    context: Context,
    private val materialList: List<MaterialReturn>,
    private val onMaterialItemClicked: PutAwayReturnMaterialListAdapter.OnMaterialItemClicked
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

    val adapter: ReturnMaterialListAdapter? = null
    private fun setUpMaterialsRecyclerView() {
        val adapter = PutAwayReturnMaterialListAdapter(materialList, onMaterialItemClicked)
        binding.materialList.adapter = adapter
    }
}