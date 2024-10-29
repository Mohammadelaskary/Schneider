package net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateReceiving.IntermediatePutAwayFragments.IntermediateFastPutAway

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Base.ResponseHandler
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayProductionWorkOrderBody
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.SignInFragment
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.IntermediateWarehouse.IntermediateWarehouseRepository
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class IntermediateFastPutAwayViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val intermediateWarehouseRepository = IntermediateWarehouseRepository()
    val getBinCodeLiveData = MutableLiveData<List<Bin>>()
    val getBinCodeStatus = MutableLiveData<StatusWithMessage>()
    fun getBinCodeData(binCode: String) {
        getBinCodeStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = receivingRepository.GetBinCodeData(
                        SignInFragment.USER_ID,
                        deviceSerialNo,
                        lang,
                        binCode
                    )
                    ResponseDataHandler(
                        response,
                        getBinCodeLiveData,
                        getBinCodeStatus,
                        application
                    ).handleData()
                } catch (ex: Exception) {
                    getBinCodeStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
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

    val fastPutAwayStatus = MutableLiveData<StatusWithMessage>()
    fun fastPutAway(body: FastPutAwayProductionWorkOrderBody) {
        fastPutAwayStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    body.deviceSerialNo = deviceSerialNo
                    body.userID = userId
                    body.applang = lang
                    val response =
                        intermediateWarehouseRepository.FastPutAwayProductionWorkOrder(body)
                    ResponseHandler(
                        response,
                        fastPutAwayStatus,
                        application
                    ).handleData()
                } catch (ex: Exception) {
                    fastPutAwayStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            fastPutAwayStatus.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL, application.getString(
                        R.string.error_in_getting_data
                    )
                )
            )
        }
    }
}