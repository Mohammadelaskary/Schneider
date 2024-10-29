package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class FastPutAwayInvoiceBody (
    @SerializedName("UserID"           ) var UserID           : String = "",
    @SerializedName("DeviceSerialNo"   ) var DeviceSerialNo   : String = "",
    @SerializedName("applang"          ) var applang          : String = "",
    @SerializedName("PoPlantHeaderId"  ) var PoPlantHeaderId  : Int?     = null,
    @SerializedName("StorageBinCode"   ) var StorageBinCode   : String?  = null,
    @SerializedName("IsFullPutaway"    ) var IsFullPutaway    : Boolean? = null,
    @SerializedName("PoPlantDetailIds" ) var PoPlantDetailIds : List<Int>  = listOf(),
)
