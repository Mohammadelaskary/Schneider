package net.gbs.schneider.Ui.Audit.AuditOrdersList

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.AuditOrder
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.Audit.AuditRepository

class AuditOrdersListViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    private val auditRepository = AuditRepository()
    val getAuditHeader: MutableLiveData<List<AuditOrder>> = MutableLiveData<List<AuditOrder>>()
    val getAuditHeaderStatus: MutableLiveData<StatusWithMessage> =
        MutableLiveData<StatusWithMessage>()

    fun getAuditOrdersList() {
        getAuditHeaderStatus.postValue(StatusWithMessage(Status.LOADING, ""))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = auditRepository.getAuditHeader(userId, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    getAuditHeader,
                    getAuditHeaderStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getAuditHeaderStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL, application.getString(
                            R.string.error_in_getting_data
                        )
                    )
                )
            }
        }
    }
}