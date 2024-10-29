package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.FastPutAway

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayInvoiceBody
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.SignIn.SignInFragment.Companion.USER_ID
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class FastPutAwayViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val repository = ReceivingRepository()
    val getBinCodeLiveData = MutableLiveData<List<Bin>>()
    val getBinCodeStatus = MutableLiveData<StatusWithMessage>()
    fun getBinCodeData(binCode: String) {
        getBinCodeStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = repository.GetBinCodeData(USER_ID, deviceSerialNo, lang, binCode)
                    ResponseDataHandler(
                        response,
                        getBinCodeLiveData,
                        getBinCodeStatus,
                        application
                    ).handleData()
                } catch (ex: Exception) {
                    getBinCodeStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL,
                            application.getString(R.string.error_in_getting_data)
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
    val fastPutAwayLiveData = MutableLiveData<Invoice>()
    fun fastPutAway(body: FastPutAwayInvoiceBody) {
        fastPutAwayStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    body.DeviceSerialNo = deviceSerialNo
                    body.UserID = userId
                    body.applang = lang
                    val response = repository.fastPutAway(body)
                    ResponseDataHandler(
                        response,
                        fastPutAwayLiveData,
                        fastPutAwayStatus,
                        application
                    ).handleData()
                } catch (ex: Exception) {
                    fastPutAwayStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL,
                            application.getString(R.string.error_in_getting_data)
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            fastPutAwayStatus.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL,
                    application.getString(R.string.error_in_getting_data)
                )
            )
        }
    }
}