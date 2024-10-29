package net.gbs.schneider.WorkOrderDetailsList

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Model.APIDataFormats.Response.GetWorkOrderList_IssueResponse

class WorkOrderDetailsListViewModel(application: Application,val activity: Activity) : BaseViewModel(application,activity) {
    val getWorkOrderList = MutableLiveData<GetWorkOrderList_IssueResponse>()

}