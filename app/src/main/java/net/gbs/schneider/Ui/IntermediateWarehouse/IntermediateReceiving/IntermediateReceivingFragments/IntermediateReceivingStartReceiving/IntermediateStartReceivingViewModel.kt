package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediateReceivingFragments.IntermediateReceivingStartReceiving

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository

class IntermediateStartReceivingViewModel(
    private val application: Application,
    activity: Activity
) : BaseViewModel(application, activity) {
    private val repository = IntermediateWarehouseRepository()
    val receiveResponse = MutableLiveData<IntermediateWorkOrder>()
    val receiveStatus = MutableLiveData<StatusWithMessage>()

    fun receiveSerialized(body: ReceiveProductionWorkOrder_SerializedBody) {
        body.userID = userId
        body.deviceSerialNo = deviceSerialNo
        body.applang = lang
        receiveStatus.postValue(StatusWithMessage(Status.LOADING))
        viewModelScope.launch {
            try {
                val response = repository.ReceiveProductionWorkOrder_Serialized(body)
                ResponseDataHandler(
                    response,
                    receiveResponse,
                    receiveStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                receiveStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_saving_data)
                    )
                )
            }
        }
    }

    fun receiveUnserialized(body: ReceiveProductionWorkOrder_UnserializedBody) {
        body.userID = userId
        body.deviceSerialNo = deviceSerialNo
        body.applang = lang
        receiveStatus.postValue(StatusWithMessage(Status.LOADING))
        viewModelScope.launch {
            try {
                val response = repository.ReceiveProductionWorkOrder_Unserialized(body)
                ResponseDataHandler(
                    response,
                    receiveResponse,
                    receiveStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                receiveStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_saving_data)
                    )
                )
            }
        }
    }

}