package net.gbs.schneider.Ui.POVendor.Receiving.PoVendorStartReceiving

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.Model.RejectionReason
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class POVendorStartReceivingViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val resultInvoiceLiveData = MutableLiveData<InvoiceVendor>()
    val resultInvoiceStatus = MutableLiveData<StatusWithMessage>()
    fun ReceiveVendor_Serialized(
        serials: List<Serial>,
        poVendorDetailId: Int,
        poVendorHeaderId: Int,
        invoiceNumber: String,
    ) {
        resultInvoiceStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveVendor_Serialized(
                    ReceiveVendor_SerializedBody(
                        DeviceSerialNo = deviceSerialNo,
                        UserID = userId,
                        serials = serials,
                        applang = lang,
                        PoVendorDetailId = poVendorDetailId,
                        PoVendorHeaderId = poVendorHeaderId,
                        InvoiceNumber = invoiceNumber,
                    )
                )
                ResponseDataHandler(
                    response,
                    resultInvoiceLiveData,
                    resultInvoiceStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                resultInvoiceStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(TAG, "ReceiveVendor_Serialized: ${ex.message.toString()}")
            }
        }
    }

    fun ReceiveVendor_Unserialized(
        receivedQty: Int,
        rejectedQty: Int,
        poVendorDetailId: Int,
        poVendorHeaderId: Int,
        invoiceNumber: String,
    ) {
        resultInvoiceStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveVendor_Unserialized(
                    ReceiveVendor_UnserializedBody(
                        DeviceSerialNo = deviceSerialNo,
                        UserID = userId,
                        receivedQty = receivedQty,
                        RejectedQty = rejectedQty,
                        applang = lang,
                        PoVendorDetailId = poVendorDetailId,
                        PoVendorHeaderId = poVendorHeaderId,
                        InvoiceNumber = invoiceNumber,
                    )
                )
                ResponseDataHandler(
                    response,
                    resultInvoiceLiveData,
                    resultInvoiceStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                resultInvoiceStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
                Log.e(TAG, "ReceiveVendor_Serialized: ${ex.message.toString()}")
            }
        }
    }

    val getRejectionRequestsLiveData = MutableLiveData<List<RejectionReason>>()
    val getRejectionRequestsStatus = MutableLiveData<StatusWithMessage>()
    fun getRejectionRequestsList() {
        getRejectionRequestsStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.getRejectionReasons(userId, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    getRejectionRequestsLiveData,
                    getRejectionRequestsStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getRejectionRequestsStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.e(TAG, "getRejectionRequestsList: ", ex)
            }
        }
    }
}