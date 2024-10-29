package net.gbs.schneider.Ui.POPlant.Receiving.POPlant.InvoiceList.StartReceiving

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveInvoice_SerializedResponse
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Model.RejectionReason
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.LoadingDialog
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools.warningDialog
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository
import retrofit2.Response

class StartReceivingViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val resultInvoiceLiveData = MutableLiveData<Response<ReceiveInvoice_SerializedResponse>>()
    fun ReceiveInvoice_Unserialized(
        invoice: Invoice,
        selectedMaterial: Material,
        receivedQty: Int,
        rejectedQty: Int,
        rejectionReasonId: Int?,
        loadingDialog: LoadingDialog
    ) {
        val body = ReceiveInvoice_UnserializedBody(
            PoPlantDetailId = selectedMaterial.poPlantDetailId,
            PoPlantHeaderId = invoice.poPlantHeaderId,
            receivedQty = receivedQty,
            RejectedQty = rejectedQty,
            applang = lang,
            DeviceSerialNo = deviceSerialNo,
            UserID = userId,
            RejectionReasonId = rejectionReasonId
        )
        loadingDialog.show()
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveInvoice_Unserialized(body)
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    resultInvoiceLiveData.postValue(response)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    warningDialog(activity, activity.getString(R.string.error_in_getting_data))
                }
            }
        }

    }

    fun ReceiveInvoice_Serialized(
        bulkQty: Int,
        isBulk: Boolean,
        rejectionReasonId: Int?,
        invoice: Invoice,
        selectedMaterial: Material,
        scannedSerialsList: MutableList<Serial>,
        loadingDialog: LoadingDialog,
        rejectedQty: Int? = null
    ) {
        val body = ReceiveInvoice_SerializedBody(
            serials = scannedSerialsList,
            PoPlantHeaderId = invoice.poPlantHeaderId,
            PoPlantDetailId = selectedMaterial.poPlantDetailId,
            applang = lang,
            DeviceSerialNo = deviceSerialNo,
            UserID = userId,
            BulkQty = bulkQty,
            IsBulk = isBulk,
            RejectionReasonId = rejectionReasonId,
            RejectedQty = rejectedQty
        )
        loadingDialog.show()
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = receivingRepository.ReceiveInvoice_Serialized(body)
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    resultInvoiceLiveData.postValue(response)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    warningDialog(activity, activity.getString(R.string.error_in_getting_data))
                }
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
                        activity.getString(R.string.error_in_getting_data)
                    )
                )
                Log.e(TAG, "getRejectionRequestsList: ", ex)
            }
        }
    }
}