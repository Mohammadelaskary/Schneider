package net.gbs.schneider.Ui.Issue.WorkOrderList

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
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.WorkOrder
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.Issue.IssueRepository

class WorkOrderListViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val issueRepository = IssueRepository()
    val workOrdersMutableLiveData = MutableLiveData<List<WorkOrder>>()
    val workOrdersStatus = MutableLiveData<StatusWithMessage>()
    fun getWorkOrders() {
        workOrdersStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    issueRepository.GetWorkOrderList_Issue(userId, deviceSerialNo, lang)
                ResponseDataHandler(
                    response,
                    workOrdersMutableLiveData,
                    workOrdersStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                workOrdersStatus.postValue(
                    StatusWithMessage(
                        Status.NETWORK_FAIL,
                        application.getString(R.string.error_in_getting_data)
                    )
                )
                Log.d(TAG, "getWorkOrders: ${ex.message}")
            }
        }
    }

}