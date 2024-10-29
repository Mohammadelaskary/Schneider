package net.gbs.schneider.Ui.GetSerialInfo

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
import net.gbs.schneider.Ui.GetItemInfo.InfoRepository

class SerialInfoViewModel(private val application: Application, activity: Activity) :
    BaseViewModel(application, activity) {
    val infoRepository = InfoRepository()
    val getSerialInfoLiveData = MutableLiveData<List<Stock>>()
    val getSerialInfoStatus = MutableLiveData<StatusWithMessage>()
    fun getSerialInfo(serialNo: String) {
        getSerialInfoStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    infoRepository.getSerialInfo(userId, deviceSerialNo, lang, serialNo)
                ResponseDataHandler(
                    response,
                    getSerialInfoLiveData,
                    getSerialInfoStatus,
                    application
                ).handleData()
            } catch (ex: Exception) {
                getSerialInfoStatus.postValue(
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