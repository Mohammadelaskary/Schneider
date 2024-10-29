package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.database.DatabaseErrorHandler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class POInvoiceListViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val invoiceMutableLiveData = MutableLiveData<List<Invoice>>()
    val getInvoicesStatus: MutableLiveData<StatusWithMessage> = MutableLiveData<StatusWithMessage>()
    fun getInvoiceList(isPOVendor:Boolean) {
        getInvoicesStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.getPOInvoiceList_Receiving(userId, deviceSerialNo, lang,isPOVendor)
                    ResponseDataHandler(response,invoiceMutableLiveData,getInvoicesStatus,application).handleData()
            } catch (ex: Exception) {
                getInvoicesStatus.postValue(
                    StatusWithMessage(
                        Status.ERROR,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.e(TAG, "getInvoiceList: ", ex)
            }
        }
    }
}
