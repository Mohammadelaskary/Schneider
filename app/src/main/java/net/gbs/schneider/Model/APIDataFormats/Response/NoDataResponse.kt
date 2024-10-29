package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName

class NoDataResponse {
    @SerializedName("responseStatus" ) var responseStatus : ResponseStatus? = ResponseStatus()
}