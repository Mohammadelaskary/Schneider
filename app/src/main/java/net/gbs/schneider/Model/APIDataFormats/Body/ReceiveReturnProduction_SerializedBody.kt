package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class ReceiveReturnProduction_SerializedBody (
    @SerializedName("userID"                   ) var userID                   : String?               = null,
    @SerializedName("deviceSerialNo"           ) var deviceSerialNo           : String?            = null,
    @SerializedName("applang"                  ) var applang                  : String?            = null,
    @SerializedName("returnReasonId"           ) var returnReasonId           : Int?               = null,
    @SerializedName("workOrderReturnId"        ) var workOrderReturnId        : Int?               = null,
    @SerializedName("workOrderReturnDetailsId" ) var workOrderReturnDetailsId : Int?               = null,
    @SerializedName("isBulk"                   ) var isBulk                   : Boolean?           = null,
    @SerializedName("bulkQty"                  ) var bulkQty                  : Int?               = null,
    @SerializedName("serials"                  ) var serials                  : List<Serial> = listOf()
)