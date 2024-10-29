package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.PoPlantInvoicesList

import android.app.Activity
import android.app.Application
import android.content.ContentValues
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

class ChangeSerialsPOInvoiceListViewModel(
    private val application: Application,
    val activity: Activity
) : BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val invoiceMutableLiveData = MutableLiveData<List<Invoice>>()

    val getInvoicesStatus: MutableLiveData<StatusWithMessage> = MutableLiveData<StatusWithMessage>()
    fun getInvoiceList() {
        getInvoicesStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.getPOInvoiceList_Receiving(userId, deviceSerialNo, lang,false)
                if (response.isSuccessful) {
                    if (response.body()?.responseStatus?.isSuccess!!) {
                        invoiceMutableLiveData.postValue(response.body()?.invoiceList!!)
                        getInvoicesStatus.postValue(StatusWithMessage(Status.SUCCESS))
                    } else {
                        invoiceMutableLiveData.postValue(listOf())
                        getInvoicesStatus.postValue(
                            StatusWithMessage(
                                Status.ERROR,
                                response.body()?.responseStatus?.statusMessage!!
                            )
                        )
                    }
                } else {
                    invoiceMutableLiveData.postValue(listOf())
                    getInvoicesStatus.postValue(
                        StatusWithMessage(
                            Status.ERROR,
                            application.getString(R.string.error_in_getting_data)
                        )
                    )
                }
            } catch (ex: Exception) {
                invoiceMutableLiveData.postValue(listOf())
                getInvoicesStatus.postValue(
                    StatusWithMessage(
                        Status.ERROR,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.e(ContentValues.TAG, "getInvoiceList: ", ex)
            }
        }
    }
}