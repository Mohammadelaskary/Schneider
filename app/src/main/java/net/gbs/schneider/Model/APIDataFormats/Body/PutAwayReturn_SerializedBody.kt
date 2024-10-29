package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial
import net.gbs.schneider.Model.SerialPutAwayReturn

data class PutAwayReturn_SerializedBody(
    @SerializedName("UserID"                   ) var UserID                   : String = "",
    @SerializedName("DeviceSerialNo"           ) var DeviceSerialNo           : String = "",
    @SerializedName("applang"                  ) var applang                  : String = "",
    @SerializedName("WorkOrderReturnId"        ) var WorkOrderReturnId        : Int?               = null,
    @SerializedName("WorkOrderReturnDetailsId" ) var WorkOrderReturnDetailsId : Int?               = null,
    @SerializedName("StorageBinCode"           ) var StorageBinCode           : String?            = null,
    @SerializedName("IsBulk"                   ) var IsBulk                   : Boolean,
    @SerializedName("BulkQty"                  ) var BulkQty                  : Int               ,
    @SerializedName("serials"                  ) var serials                  : List<SerialPutAwayReturn> = arrayListOf()
)