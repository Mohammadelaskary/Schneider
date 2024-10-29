package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class PutAwayInvoice_SerializedBody(
    @SerializedName("UserID"          ) var UserID          : String             = "",
    @SerializedName("DeviceSerialNo"  ) var DeviceSerialNo  : String             = "",
    @SerializedName("applang"         ) var applang         : String             = "",
    @SerializedName("PoPlantHeaderId" ) var PoPlantHeaderId : Int?               = null,
    @SerializedName("PoPlantDetailId" ) var PoPlantDetailId : Int?               = null,
    @SerializedName("StorageBinCode"  ) var StorageBinCode  : String?            = null,
    @SerializedName("IsBulk"          ) var IsBulk          : Boolean?           = null,
    @SerializedName("BulkQty"         ) var BulkQty         : Int?               = null,
    @SerializedName("serials"         ) var serials         : List<Serial> = arrayListOf()
)