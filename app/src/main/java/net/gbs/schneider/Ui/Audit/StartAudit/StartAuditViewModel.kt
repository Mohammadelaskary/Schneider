package net.gbs.schneider.Ui.Audit.StartAudit

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Base.ResponseHandler
import net.gbs.schneider.Model.APIDataFormats.Body.SaveAuditDetailsBody
import net.gbs.schneider.Model.AuditDetails
import net.gbs.schneider.Model.Bin
import net.gbs.schneider.Model.MaterialData
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class StartAuditViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    private val receivingRepository = ReceivingRepository()
    val getBinDataLiveData = MutableLiveData<List<Bin>>()
    val getBinDataStatus = MutableLiveData<StatusWithMessage>()
    fun getBinData(binCode: String) {
        getBinDataStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    receivingRepository.GetBinCodeData(userId, deviceSerialNo, lang, binCode)
                ResponseDataHandler(
                    response,
                    getBinDataLiveData,
                    getBinDataStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getBinDataStatus.postValue(StatusWithMessage(Status.LOADING, ""))
            }
        }
    }

    val getMaterialDataLiveData = MutableLiveData<MaterialData>()
    val getMaterialDataStatus = MutableLiveData<StatusWithMessage>()
    fun getMaterialData(MaterialCode: String) {
        getMaterialDataStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = receivingRepository.GetMaterialCodeData(
                userId,
                deviceSerialNo,
                lang,
                MaterialCode
            )
            ResponseDataHandler(
                response,
                getMaterialDataLiveData,
                getMaterialDataStatus,
                application
            ).handleData()
        }
    }

    val checkSerialStatus = MutableLiveData<StatusWithMessage>()
    val serialLiveData = MutableLiveData<String>()
    fun checkSerial(MaterialCode: String, serialCode: String) {
        getMaterialDataStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = receivingRepository.checkSerialCode(
                userId,
                deviceSerialNo,
                lang,
                MaterialCode,
                serialCode
            )
            ResponseHandler(response, checkSerialStatus, application).handleData()
            serialLiveData.postValue(serialCode)
        }
    }

    val saveAuditStatus = MutableLiveData<StatusWithMessage>()
    val saveAuditLiveData = MutableLiveData<List<AuditDetails>>()
    fun saveAudit(
        materialCode: String,
        serials: MutableList<String>,
        qty: Int,
        readBarcode: Int,
        projectNumber: String,
        binCode: String,
        auditHeaderId: Int
    ) {
        saveAuditStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        val saveAuditBody = SaveAuditDetailsBody(
            applang = lang,
            DeviceSerialNo = deviceSerialNo,
            UserID = userId,
            StorageBinCode = binCode,
            MaterialCode = materialCode,
            AuditHeaderId = auditHeaderId,
            ProjectNumber = projectNumber,
            Qty = qty,
            ReadBarcode = readBarcode,
            Serials = serials,
        )
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = receivingRepository.saveAuditDetails(saveAuditBody)
            ResponseDataHandler(
                response,
                saveAuditLiveData,
                checkSerialStatus,
                application
            ).handleData()
        }
    }
}