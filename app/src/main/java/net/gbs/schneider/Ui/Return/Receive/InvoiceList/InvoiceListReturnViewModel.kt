package net.gbs.schneider.Ui.Return.Receive.InvoiceList

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.WorkOrderReturn
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class InvoiceListReturnViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val gettingInvoiceListLiveData = MutableLiveData<List<WorkOrderReturn>>()
    val gettingInvoiceListStatus = MutableLiveData<StatusWithMessage>()
    fun getInvoiceList() {
        gettingInvoiceListStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.GetReturnList_Receiving(userId, deviceSerialNo, lang)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        gettingInvoiceListStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        gettingInvoiceListLiveData.postValue(response.body()?.getData())
                    } else {
                        gettingInvoiceListStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    gettingInvoiceListStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                gettingInvoiceListStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(TAG, "getInvoiceList: ${ex.message}")
            }
        }
    }

}