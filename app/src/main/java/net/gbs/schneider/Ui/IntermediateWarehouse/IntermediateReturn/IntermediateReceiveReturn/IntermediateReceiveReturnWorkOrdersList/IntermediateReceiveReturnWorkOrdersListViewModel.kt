package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReturn.IntermediateReceiveReturn.IntermediateReceiveReturnWorkOrdersList

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository

class IntermediateReceiveReturnWorkOrdersListViewModel(
    private val application: Application,
    activity: Activity
) : BaseViewModel(application, activity) {
    val repository = IntermediateWarehouseRepository()
    val gettingWorkOrdersListLiveData = MutableLiveData<List<IntermediateWorkOrderReturn>>()
    val gettingWorkOrdersListStatus = MutableLiveData<StatusWithMessage>()
    fun getWorkOrdersList() {
        gettingWorkOrdersListStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    repository.GetReturnProductionList_Receiving(userId, deviceSerialNo, lang)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        gettingWorkOrdersListStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        gettingWorkOrdersListLiveData.postValue(response.body()?.getData())
                    } else {
                        gettingWorkOrdersListStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    gettingWorkOrdersListStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                gettingWorkOrdersListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(ContentValues.TAG, "getInvoiceList: ${ex.message}")
            }
        }
    }

}