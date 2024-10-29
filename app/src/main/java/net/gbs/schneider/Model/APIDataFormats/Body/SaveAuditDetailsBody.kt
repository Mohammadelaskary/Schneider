package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class SaveAuditDetailsBody (
    @SerializedName("UserID"         ) var UserID         : String?              = null,
    @SerializedName("DeviceSerialNo" ) var DeviceSerialNo : String?           = null,
    @SerializedName("applang"        ) var applang        : String?           = null,
    @SerializedName("AuditHeaderId"  ) var AuditHeaderId  : Int?              = null,
    @SerializedName("MaterialCode"   ) var MaterialCode   : String?           = null,
    @SerializedName("StorageBinCode" ) var StorageBinCode : String?           = null,
    @SerializedName("ProjectNumber"  ) var ProjectNumber  : String?           = null,
    @SerializedName("Qty"            ) var Qty            : Int?              = null,
    @SerializedName("ReadBarcode"    ) var ReadBarcode    : Int?              = null,
    @SerializedName("Serials"        ) var Serials        : MutableList<String> = arrayListOf()
)
