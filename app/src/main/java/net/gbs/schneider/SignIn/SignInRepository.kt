package net.gbs.schneider.SignIn

import android.app.Activity
import net.gbs.schneider.Base.BaseRepository
import net.gbs.schneider.Model.APIDataFormats.Body.SignInBody

class SignInRepository  : BaseRepository() {
    suspend fun signIn(signInBody: SignInBody) = apiInterface.signIn(signInBody)
    suspend fun getPlantList(userId:String,deviceSerialNo:String,appLang:String) = apiInterface.getPlantList(userId,deviceSerialNo,appLang)
    suspend fun getWarehouseList(userId:String,deviceSerialNo:String,appLang:String,plantId:Int) = apiInterface.getWarehouseList(userId,deviceSerialNo,appLang,plantId)
}