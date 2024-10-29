package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class ReceiveReturnProduction_UnserializedBody (
    @SerializedName("userID"                   ) var userID                   : String?    = null,
    @SerializedName("deviceSerialNo"           ) var deviceSerialNo           : String? = null,
    @SerializedName("applang"                  ) var applang                  : String? = null,
    @SerializedName("returnReasonId"           ) var returnReasonId           : Int?    = null,
    @SerializedName("workOrderReturnId"        ) var workOrderReturnId        : Int?    = null,
    @SerializedName("workOrderReturnDetailsId" ) var workOrderReturnDetailsId : Int?    = null,
    @SerializedName("receivedQty"              ) var receivedQty              : Int?    = null
)
