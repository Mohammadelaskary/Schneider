package net.gbs.schneider.Base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.gbs.schneider.Model.StatusWithMessage
import net.gbs.schneider.R
import net.gbs.schneider.Tools.Status
import retrofit2.Response

class ResponseDataHandler<T :BaseResponse<D> ,D> (val response: Response<T>,val responseData :MutableLiveData<D>,val statusWithMessage: MutableLiveData<StatusWithMessage>,val application: Application){
    fun handleData(){
        if (response.isSuccessful){
            if (response.body()?.responseStatus?.isSuccess!!){
                responseData.postValue(response.body()?.getData())
                statusWithMessage.postValue(StatusWithMessage(Status.SUCCESS,response.body()?.responseStatus?.statusMessage!!))
            } else {
                statusWithMessage.postValue(StatusWithMessage(Status.ERROR,response.body()?.responseStatus?.statusMessage!!))
            }
        } else {
            statusWithMessage.postValue(StatusWithMessage(Status.NETWORK_FAIL,application.getString(
                R.string.error_in_getting_data)))
        }
    }
}