package net.gbs.schneider.SignIn

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import net.gbs.schneider.Base.BaseFragmentWithViewModel
import net.gbs.schneider.Model.Plant
import net.gbs.schneider.Model.Warehouse
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_ID
import net.gbs.schneider.Tools.LoadingDialog
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import net.gbs.schneider.Tools.Tools.NavigateTo
import net.gbs.schneider.Tools.Tools.attachButtonsToListener
import net.gbs.schneider.Tools.Tools.back
import net.gbs.schneider.Tools.Tools.clearInputLayoutError
import net.gbs.schneider.ViewModelFactories.BaseViewModelFactory
import net.gbs.schneider.databinding.FragmentLocationBinding

class LocationDialog(val viewModel:SignInViewModel, val onLocationSaved: OnLocationSaved) : DialogFragment(),OnClickListener {


    private lateinit var binding:FragmentLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val plant = viewModel.getPlantFromLocalStorage()
        if (plant!=null){
            binding.plantsSpinner.setText(plant.plantName,false)
        }
        val warehouse = viewModel.getWarehouseFromLocalStorage()
        if (warehouse!=null){
            binding.warehousesSpinner.setText(warehouse.warehouseName,false)
        }
        attachButtonsToListener(this,binding.save)
        clearInputLayoutError(binding.plant,binding.warehouse)
        setUpSpinners()
        observeGettingPlantList()
        observeGettingWarehouseList()
    }

    private fun observeGettingWarehouseList() {
        viewModel.warehouseListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> binding.warehouseLoading.show()
                Status.SUCCESS -> binding.warehouseLoading.hide()
                else -> {
                    binding.warehouseLoading.hide()
                    binding.warehouse.error = it.message
                }
            }
        }
        viewModel.warehouseListLiveData.observe(requireActivity()){
            warehouseList = it
            Log.d(TAG, "observeGettingWarehouseList: ${warehouseList.size}")
            warehouseListAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,warehouseList)
            binding.warehousesSpinner.setAdapter(warehouseListAdapter)
        }
    }

    private lateinit var plantListAdapter:ArrayAdapter<Plant>
    private var plantsList = listOf<Plant>()
    private var selectedPlant:Plant? = null
    private lateinit var warehouseListAdapter:ArrayAdapter<Warehouse>
    private var warehouseList = listOf<Warehouse>()
    private var selectedWarehouse:Warehouse? = null
    private fun setUpSpinners() {
        binding.plantsSpinner.setOnItemClickListener { adapterView, view, position, l ->
            selectedPlant = plantsList[position]
            binding.warehousesSpinner.setText("")
            selectedWarehouse = null
            viewModel.getWarehouseList(selectedPlant?.plantId!!)
        }
        binding.warehousesSpinner.setOnItemClickListener { adapterView, view, position, l ->
            selectedWarehouse = warehouseList[position]
        }
    }

    private fun observeGettingPlantList() {
        viewModel.plantListStatus.observe(requireActivity()){
            when(it.status){
                Status.LOADING -> binding.plantLoading.show()
                Status.SUCCESS -> binding.plantLoading.hide()
                else -> {
                    binding.plantLoading.hide()
                    binding.plant.error = it.message
                }
            }
        }
        viewModel.plantListLiveData.observe(requireActivity()){
            plantsList = it
            Log.d(TAG, "observeGettingPlantList: ${plantsList.size}")
            plantListAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,plantsList)
            binding.plantsSpinner.setAdapter(plantListAdapter)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.save ->{
                if (selectedPlant!=null){
                    if (selectedWarehouse!=null){
                        viewModel.savePlantToLocalStorage(selectedPlant!!)
                        viewModel.saveWarehouseToLocalStorage(selectedWarehouse!!)
                        Tools.showSuccessAlerter(getString(R.string.saved_successfully),requireActivity())
//                        if (USER_ID!=null){
//                            NavigateTo(v,R.id.action_locationFragment_to_mainMenuFragment2)
//                        } else dismiss()
                        onLocationSaved.onLocationSaved()
                    } else binding.warehouse?.error = getString(R.string.please_select_warehouse)
                } else binding.plant?.error = getString(R.string.please_select_plant)
            }
        }
    }
    interface OnLocationSaved {
        fun onLocationSaved()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlantList()
    }
}