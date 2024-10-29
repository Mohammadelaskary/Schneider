package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class ReceiveReturn_SerializedBody(
    @SerializedName("UserID"                   ) var UserID                   : String             = "",
    @SerializedName("DeviceSerialNo"           ) var DeviceSerialNo           : String             = "",
    @SerializedName("applang"                  ) var applang                  : String             = "",
    @SerializedName("WorkOrderReturnId"        ) var WorkOrderReturnId        : Int,
    @SerializedName("WorkOrderReturnDetailsId" ) var WorkOrderReturnDetailsId : Int,
    @SerializedName("IsBulk"                   ) var IsBulk                   : Boolean,
    @SerializedName("BulkQty"                  ) var BulkQty                  : Int,
    @SerializedName("ReturnReasonId"                 ) var reasonId                 : Int,
    @SerializedName("serials"                  ) var serials                  : List<Serial>       = arrayListOf()
)