package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class ReceiveReturn_UnserializedBody(
    @SerializedName("UserID"                   ) var UserID                   : String?    = null,
    @SerializedName("DeviceSerialNo"           ) var DeviceSerialNo           : String? = null,
    @SerializedName("applang"                  ) var applang                  : String? = null,
    @SerializedName("WorkOrderReturnId"        ) var WorkOrderReturnId        : Int?    = null,
    @SerializedName("WorkOrderReturnDetailsId" ) var WorkOrderReturnDetailsId : Int?    = null,
    @SerializedName("ReturnReasonId"           ) var ReturnReasonId           : Int?    = null,
    @SerializedName("ReceivedQty"              ) var ReceivedQty              : Int?    = null
)