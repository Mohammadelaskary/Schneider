package net.gbs.schneider.Ui.Return.Receive.StartReturn

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_UnserializedBody
import net.gbs.schneider.Model.ReturnReason
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class StartReturnViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val returnResponseStatus = MutableLiveData<StatusWithMessage>()
    val returnResponse = MutableLiveData<WorkOrderReturn>()
    fun receiveReturnSerialized(body: ReceiveReturn_SerializedBody) {
        body.UserID = userId
        body.DeviceSerialNo = deviceSerialNo
        body.applang = lang
        returnResponseStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveReturn_Serialized(body)
                ResponseDataHandler(
                    response,
                    returnResponse,
                    returnResponseStatus,
                    application,
                ).handleData()
            } catch (ex: Exception) {
                returnResponseStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
            }
        }

    }

    fun receiveReturnUnserialized(body: ReceiveReturn_UnserializedBody) {
        body.UserID = userId
        body.DeviceSerialNo = deviceSerialNo
        body.applang = lang
        returnResponseStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveReturn_Unserialized(body)
                ResponseDataHandler(
                    response,
                    returnResponse,
                    returnResponseStatus,
                    application,
                ).handleData()
            } catch (ex: Exception) {
                returnResponseStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
            }
        }

    }

    val getReturnReasonListLiveData = MutableLiveData<List<ReturnReason>>()
    val getReturnReasonListStatus = MutableLiveData<StatusWithMessage>()
    fun getReturnReasonList() {
        getReturnReasonListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.getReturnReasonList(userId, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    getReturnReasonListLiveData,
                    getReturnReasonListStatus,
                    application
                ).handleData()
            } catch (ex: java.lang.Exception) {
                getReturnReasonListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.e(ContentValues.TAG, "getReturnReasonList: ${ex.message}")
            }
        }
    }
}