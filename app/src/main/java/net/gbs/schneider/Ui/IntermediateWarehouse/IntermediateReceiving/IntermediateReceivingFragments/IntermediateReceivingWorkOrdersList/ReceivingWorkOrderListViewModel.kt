package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingWorkOrdersList

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_ID
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository

class ReceivingWorkOrderListViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    private val repository = IntermediateWarehouseRepository()

    val getWorkOrderList = MutableLiveData<List<IntermediateWorkOrder>>()
    val getWorkOrderListStatus = MutableLiveData<StatusWithMessage>()

    fun getWorkOrderList() {
        getWorkOrderListStatus.postValue(StatusWithMessage(Status.LOADING))
        viewModelScope.launch {
            try {
                val response = repository.getWorkOrdersList(USER_ID, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    getWorkOrderList,
                    getWorkOrderListStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                Log.e("ReceivingWorkOrderListViewModel", "getWorkOrderList: ", ex)
                getWorkOrderListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
            }
        }
    }
}