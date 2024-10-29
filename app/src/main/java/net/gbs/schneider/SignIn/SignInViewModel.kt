package net.gbs.schneider.SignIn

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.schneider.Base.BaseViewModel
import net.gbs.schneider.Base.ResponseDataHandler
import net.gbs.schneider.Model.APIDataFormats.Body.SignInBody
import net.gbs.schneider.Model.APIDataFormats.User
import net.gbs.schneider.Model.Plant
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.Model.Warehouse
import net.gbs.schneider.R
import net.gbs.schneider.Tools.LoadingDialog
import net.gbs.schneider.Tools.Status
import net.gbs.schneider.Tools.Tools
import java.lang.Exception

class SignInViewModel (private val application: Application,activity: Activity): BaseViewModel(application,activity) {
    val signInRepository = SignInRepository()
    val signInLiveData = MutableLiveData<User>()
    val signInStatus   = MutableLiveData<StatusWithMessage>()
    fun signIn(userName: String, password :String){
        signInStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = signInRepository.signIn(
                    SignInBody(
                        password,
                        userName
                    )
                )
                ResponseDataHandler(response,signInLiveData,signInStatus,application).handleData()
            } catch (ex :Exception){
                signInStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
                Log.e(TAG, "signIn: ${ex.message}", )
            }
        }
    }
    val plantListLiveData:MutableLiveData<List<Plant>> = MutableLiveData()
    val warehouseListLiveData:MutableLiveData<List<Warehouse>> = MutableLiveData()
    val plantListStatus:MutableLiveData<StatusWithMessage> = MutableLiveData()
    val warehouseListStatus:MutableLiveData<StatusWithMessage> = MutableLiveData()
    fun getPlantList (){
        plantListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {

                try {
                    val response = signInRepository.getPlantList(userId,deviceSerialNo,lang)
                    if (response.isSuccessful){
                        if (response.body()?.responseStatus?.isSuccess!!){
                            if (response.body()?.getData()!=null){
                                plantListLiveData.postValue(response.body()?.getData())
                                plantListStatus.postValue(StatusWithMessage(Status.SUCCESS))
                            }
                        } else {
                            plantListStatus.postValue(StatusWithMessage(Status.ERROR,response.body()?.responseStatus?.statusMessage!!))
                        }
                    } else {
                        plantListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
                    }
                } catch (ex: Exception) {
                    plantListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
                    Log.d(TAG, "getPlantList: ${ex.message}")
                }
            }
        }

    fun getWarehouseList (plantId:Int){
        warehouseListStatus.postValue(StatusWithMessage(Status.LOADING))
        job = CoroutineScope(Dispatchers.IO).launch {

                try {
                    val response = signInRepository.getWarehouseList(userId,deviceSerialNo,lang,plantId)
                    if (response.isSuccessful){
                        if (response.body()?.responseStatus?.isSuccess!!){
                            if (response.body()?.getData()!=null){
                                warehouseListLiveData.postValue(response.body()?.getData())
                                warehouseListStatus.postValue(StatusWithMessage(Status.SUCCESS))
                            }
                        } else {
                            warehouseListStatus.postValue(StatusWithMessage(Status.ERROR,response.body()?.responseStatus?.statusMessage!!))
                        }
                    } else {
                        warehouseListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
                    }
                } catch (ex: Exception) {
                    warehouseListStatus.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(R.string.error_in_getting_data)))
                    Log.d(TAG, "getWarehouseList: ${ex.message}")
                }

        }

    }
}