package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName

data class ResponseStatus (
    @SerializedName("isSuccess"     ) var isSuccess     : Boolean? = null,
    @SerializedName("statusCode"    ) var statusCode    : Int?     = null,
    @SerializedName("statusMessage" ) var statusMessage : String?  = null,
    @SerializedName("errorMessage"  ) var errorMessage  : String?  = null
)