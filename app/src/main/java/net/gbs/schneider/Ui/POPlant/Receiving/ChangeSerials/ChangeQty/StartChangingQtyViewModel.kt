package net.gbs.schneider.Ui.POPlant.Receiving.ChangeSerials.ChangeQty.StartChangingSerialsViewModel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseHandler
import net.gbs.schneider.Model.APIDataFormats.Body.ChangeQuantityRequest_POFactoryBody
import net.gbs.schneider.Model.Invoice
import net.gbs.schneider.Model.Material
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Ui.POPlant.Receiving.ReceivingRepository

class StartChangingQtyViewModel(private val application: Application, val activity: Activity) :
    BaseViewModel(application, activity) {
    val receivingRepository = ReceivingRepository()
    val changeQtyStatus = MutableLiveData<StatusWithMessage>()
    fun changeSerials(
        invoice: Invoice,
        selectedMaterial: Material,
        qty: Int
    ) {
        val body = ChangeQuantityRequest_POFactoryBody(
            PoPlantDetailId = selectedMaterial.poPlantDetailId,
            PoPlantHeaderId = invoice.poPlantHeaderId,
            applang = lang,
            DeviceSerialNo = deviceSerialNo,
            UserID = userId,
            PoPlantDetailQty = qty
        )
        changeQtyStatus.postValue(StatusWithMessage(Status.LOADING))
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = receivingRepository.changeQty(body)
                    ResponseHandler(response, changeQtyStatus, application).handleData()
                } catch (ex: Exception) {
                    changeQtyStatus.postValue(
                        StatusWithMessage(
                            Status.NETWORK_FAIL, application.getString(
                                R.string.error_in_getting_data
                            )
                        )
                    )
                    Log.e(this.javaClass.name, "changeQty: ", ex)
                }
            }
        } catch (ex: Exception) {
            changeQtyStatus.postValue(
                StatusWithMessage(
                    Status.NETWORK_FAIL, application.getString(
                        R.string.error_in_getting_data
                    )
                )
            )
            Log.e(this.javaClass.name, "changeQty: ", ex)
        }
    }

}