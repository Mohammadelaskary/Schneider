package net.gbs.schneider.Base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Job
import net.gbs.schneider.IntentKeys.LOCATION_KEY
import net.gbs.schneider.IntentKeys.PLANT_KEY
import net.gbs.schneider.IntentKeys.WAREHOUSE_KEY
import net.gbs.schneider.Model.Plant
import net.gbs.schneider.Model.Warehouse
import net.gbs.schneider.SignIn.SignInFragment
import net.gbs.schneider.Tools.LocaleHelper

open class BaseViewModel(application: Application,activity: Activity): AndroidViewModel(application) {
    var job: Job? = null
    val lang = LocaleHelper.getLanguage(application.applicationContext)!!
    val deviceSerialNo = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    val userId = SignInFragment.USER_ID

    val sharedPref: SharedPreferences =
        application.getSharedPreferences(LOCATION_KEY, Context.MODE_PRIVATE)
    fun savePlantToLocalStorage (plant: Plant){
        sharedPref.edit().putString(PLANT_KEY,Plant.toJson(plant)).apply()
    }
    fun saveWarehouseToLocalStorage (warehouse: Warehouse){
        sharedPref.edit().putString(WAREHOUSE_KEY,Warehouse.toJson(warehouse)).apply()
    }
    fun getPlantFromLocalStorage ():Plant? {
        val plant = sharedPref.getString(PLANT_KEY,null)
        return if (plant == null)
            null
        else
            Plant.fromJson(plant)
    }
    fun getWarehouseFromLocalStorage ():Warehouse? {
        val warehouse = sharedPref.getString(WAREHOUSE_KEY,null)
        return if (warehouse == null)
            null
        else
            Warehouse.fromJson(warehouse)
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}