package net.gbs.schneider.Ui.Issue.StartIssuing

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_UnserializedBody
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.Issue.IssueRepository

class StartIssuingViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val issueRepository = IssueRepository()
    val resultWorkOrderStatus = MutableLiveData<StatusWithMessage>()
    val resultWorkOrder = MutableLiveData<WorkOrder>()
    val getBinCodeStatus = MutableLiveData<StatusWithMessage>()
    val getBinCodeData = MutableLiveData<Bin>()
    fun issueWorkOrderSerialized(issueworkorderSerializedbody: IssueWorkOrder_SerializedBody) {
        resultWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        issueworkorderSerializedbody.DeviceSerialNo = deviceSerialNo
        issueworkorderSerializedbody.UserID = userId
        issueworkorderSerializedbody.applang = lang
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    issueRepository.issueWorkOrder_Serialized(issueworkorderSerializedbody)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        resultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        resultWorkOrder.postValue(response.body()?.workOrder)
                    } else {
                        resultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    resultWorkOrderStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL,
                            application.getString(R.string.error_in_getting_data)
                        )
                    )
                }
            } catch (ex: Exception) {
                resultWorkOrderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.d(TAG, "issueWorkOrderSerialized: ${ex.message}")
            }
        }
    }

    fun getBinCodeData(binCode: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            resultWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
            try {
                val response =
                    issueRepository.GetBinCodeData(userId, deviceSerialNo, lang, binCode)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getBinCodeStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getBinCodeData.postValue(response.body()?.getData()?.get(0))
                    } else {
                        getBinCodeStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getBinCodeStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL,
                            application.getString(R.string.error_in_getting_data)
                        )
                    )
                }
            } catch (ex: Exception) {
                getBinCodeStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
            }
        }
    }

    fun issueWorkOrderUnserialized(issueworkorderUnserializedbody: IssueWorkOrder_UnserializedBody) {
        issueworkorderUnserializedbody.DeviceSerialNo = deviceSerialNo
        issueworkorderUnserializedbody.UserID = userId
        issueworkorderUnserializedbody.applang = lang
        resultWorkOrderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    issueRepository.issueWorkOrder_Unserialized(issueworkorderUnserializedbody)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        resultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        resultWorkOrder.postValue(response.body()?.workOrder)
                    } else {
                        resultWorkOrderStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    resultWorkOrderStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL,
                            application.getString(R.string.error_in_getting_data)
                        )
                    )
                }
            } catch (ex: Exception) {
                resultWorkOrderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.d(TAG, "issueWorkOrderSerialized: ${ex.message}")
            }
        }
    }

}