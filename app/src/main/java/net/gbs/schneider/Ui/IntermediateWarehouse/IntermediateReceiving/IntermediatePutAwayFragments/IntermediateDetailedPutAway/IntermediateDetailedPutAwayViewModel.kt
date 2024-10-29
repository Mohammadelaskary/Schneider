package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateDetailedPutAway

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class IntermediateDetailedPutAwayViewModel(
    private val application: Application,
    activity: Activity
) : BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val intermediateWarehouseRepository = IntermediateWarehouseRepository()
    val getBinData = MutableLiveData<Bin>()
    val getBinDataStatus = MutableLiveData<StatusWithMessage>()
    val getResultWorkOrder = MutableLiveData<IntermediateWorkOrder>()
    val getResultWorkOrderStatus = MutableLiveData<StatusWithMessage>()
    fun getBinData(binCode: String) {
        getBinDataStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.GetBinCodeData(userId, deviceSerialNo, lang, binCode)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getBinDataStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getBinData.postValue(response.body()?.getData()?.get(0))
                    } else {
                        getBinDataStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getBinDataStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getBinDataStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(ContentValues.TAG, "getBinData: ${ex.message}")
            }
        }
    }

    fun putAwayInvoice_Serialized(body: PutAwayProductionWorkOrder_SerializedBody) {
        body.deviceSerialNo = deviceSerialNo
        body.userID = userId
        body.applang = lang
        getResultWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    intermediateWarehouseRepository.PutAwayProductionWorkOrder_Serialized(body)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getResultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getResultWorkOrder.postValue(response.body()?.getData())
                    } else {
                        getResultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getResultWorkOrderStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getResultWorkOrderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(ContentValues.TAG, "putAwayInvoice_Serialized: ${ex.message}")
            }
        }
    }

    fun putAwayInvoice_Unserialized(body: PutAwayProductionWorkOrder_UnserializedBody) {
        body.deviceSerialNo = deviceSerialNo
        body.userID = userId
        body.applang = lang
        getResultWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            val response =
                intermediateWarehouseRepository.PutAwayProductionWorkOrder_Unserialized(body)
            try {
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getResultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getResultWorkOrder.postValue(response.body()?.getData())
                    } else {
                        getResultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getResultWorkOrderStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getResultWorkOrderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(ContentValues.TAG, "putAwayInvoice_Serialized: ${ex.message}")
            }
        }
    }
}