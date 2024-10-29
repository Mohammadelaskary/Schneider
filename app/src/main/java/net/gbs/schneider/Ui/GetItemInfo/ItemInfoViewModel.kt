package net.gbs.schneider.Ui.GetItemInfo

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.Stock
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status

class ItemInfoViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val infoRepository = InfoRepository()
    val getItemInfoLiveData = MutableLiveData<List<Stock>>()
    val getItemInfoStatus = MutableLiveData<StatusWithMessage>()
    fun getItemInfo(materialCode: String) {
        getItemInfoStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    infoRepository.getItemInfo(userId, deviceSerialNo, lang, materialCode)
                ResponseDataHandler(
                    response,
                    getItemInfoLiveData,
                    getItemInfoStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getItemInfoStatus.postValue(
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