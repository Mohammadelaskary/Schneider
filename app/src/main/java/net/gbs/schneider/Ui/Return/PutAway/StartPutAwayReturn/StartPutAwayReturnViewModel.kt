package net.gbs.schneider.Ui.Return.PutAway.StartPutAwayReturn

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_UnserializedBody
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class StartPutAwayReturnViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val getBinData = MutableLiveData<Bin>()
    val getBinDataStatus = MutableLiveData<StatusWithMessage>()
    val getResultInvoice = MutableLiveData<WorkOrderReturn>()
    val getResultInvoiceStatus = MutableLiveData<StatusWithMessage>()
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
                Log.e(TAG, "getBinData: ${ex.message}")
            }
        }
    }

    fun putAwayInvoice_Serialized(body: PutAwayReturn_SerializedBody) {
        body.DeviceSerialNo = deviceSerialNo
        body.UserID = userId
        body.applang = lang
        getResultInvoiceStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.PutAwayReturn_Serialized(body)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getResultInvoiceStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getResultInvoice.postValue(response.body()?.getData())
                    } else {
                        getResultInvoiceStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getResultInvoiceStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getResultInvoiceStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(TAG, "putAwayInvoice_Serialized: ${ex.message}")
            }
        }
    }

    fun putAwayInvoice_Unserialized(body: PutAwayReturn_UnserializedBody) {
        body.DeviceSerialNo = deviceSerialNo
        body.UserID = userId
        body.applang = lang
        getResultInvoiceStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.PutAwayReturn_Unserialized(body)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getResultInvoiceStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getResultInvoice.postValue(response.body()?.getData())
                    } else {
                        getResultInvoiceStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getResultInvoiceStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getResultInvoiceStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(TAG, "putAwayInvoice_Serialized: ${ex.message}")
            }
        }
    }
}