package com.example.mobco.Repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import net.gbs.schneider.R

class LocalStorage (val activity: Activity) {
    val preference: SharedPreferences =activity.getSharedPreferences(activity.getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor=preference.edit()

}
