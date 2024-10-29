package net.gbs.schneider.Base

import android.app.Activity
import android.provider.Settings
import com.example.mobco.Repository.LocalStorage
import net.gbs.scheinder.Repository.ApiFactory
import net.gbs.schneider.Repository.ApiInterface
import net.gbs.schneider.SignIn.SignInFragment
import net.gbs.schneider.Tools.LocaleHelper

open class BaseRepository {
    var apiInterface = ApiFactory.getInstance()?.create(ApiInterface::class.java)!!
}