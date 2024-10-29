package net.gbs.schneider.Ui.POPlant.Receiving.PutAway.InvoiceListPutAway

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class InvoiceListPutAwayViewModel(
    private val application: Application,
    activity: Activity
) : BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val getInvoiceList = MutableLiveData<List<Invoice>>()
    val getInvoiceListStatus = MutableLiveData<StatusWithMessage>()
    fun getInvoiceList() {
        getInvoiceListStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.getPOInvoiceList_PutAway(userId, deviceSerialNo, lang)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        getInvoiceListStatus.postValue(
                            StatusWithMessage(
                                Status.SUCCESS,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                        getInvoiceList.postValue(response.body()?.getData())
                    } else {
                        getInvoiceList.postValue(listOf())
                        getInvoiceListStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    getInvoiceList.postValue(listOf())
                    getInvoiceListStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                getInvoiceList.postValue(listOf())
                getInvoiceListStatus.postValue(
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