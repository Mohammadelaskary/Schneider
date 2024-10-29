package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.SerialWithStatus

data class ChangeSerialRequest_POFactoryBody(
    @SerializedName("UserID"           ) var UserID           : String?               = null,
    @SerializedName("DeviceSerialNo"   ) var DeviceSerialNo   : String?            = null,
    @SerializedName("applang"          ) var applang          : String?            = null,
    @SerializedName("PoPlantHeaderId"  ) var PoPlantHeaderId  : Int?               = null,
    @SerializedName("PoPlantDetailId"  ) var PoPlantDetailId  : Int?               = null,
    @SerializedName("serials"          ) var serials          : List<SerialWithStatus> = arrayListOf()
)