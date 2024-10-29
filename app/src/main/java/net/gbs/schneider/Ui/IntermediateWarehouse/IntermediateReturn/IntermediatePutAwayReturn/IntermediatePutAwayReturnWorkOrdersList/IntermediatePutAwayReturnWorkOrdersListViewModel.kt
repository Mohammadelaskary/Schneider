package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediatePutAwayReturn.IntermediatePutAwayReturnWorkOrdersList

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository

class IntermediatePutAwayReturnWorkOrdersListViewModel(
    private val application: Application,
    activity: Activity
) : BaseViewModel(application, activity) {
    val getPutAwayWorkOrderLiveData = MutableLiveData<List<IntermediateWorkOrderReturn>>()
    val getPutAwayWorkOrderStatus = MutableLiveData<StatusWithMessage>()
    val repository = IntermediateWarehouseRepository()
    fun getWorkOrderList() {
        getPutAwayWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    repository.GetReturnList_PutAway(userId, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    getPutAwayWorkOrderLiveData,
                    getPutAwayWorkOrderStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getPutAwayWorkOrderStatus.postValue(
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