package net.gbs.schneider.Ui.POVendor.Receiving.PoVendorList

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class POVendorListPutAwayViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val getPoVendorListLiveData = MutableLiveData<List<InvoiceVendor>>()
    val getPoVendorListStatus = MutableLiveData<StatusWithMessage>()
    fun getPoVendorInvoiceList() {
        getPoVendorListStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        receivingRepository.getPOVendorList_PutAway(userId, deviceSerialNo, lang)
                    ResponseDataHandler(
                        response,
                        getPoVendorListLiveData,
                        getPoVendorListStatus,
                        application
                    ).handleData()
                } catch (ex: Exception) {
                    getPoVendorListStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            getPoVendorListStatus.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL, application.getString(
                        R.string.error_in_getting_data
                    )
                )
            )
        }
    }
}