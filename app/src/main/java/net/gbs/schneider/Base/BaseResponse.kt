package net.gbs.schneider.Base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.APIDataFormats.Response.ResponseStatus

abstract class BaseResponse<T> {
    @SerializedName("responseStatus")
    @Expose
    var responseStatus: ResponseStatus? = null

    abstract fun getData(): T
}