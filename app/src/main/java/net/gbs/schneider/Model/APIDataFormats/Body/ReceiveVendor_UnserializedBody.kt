package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class ReceiveVendor_UnserializedBody (
    @SerializedName("UserID"           ) var UserID           : String?    = null,
    @SerializedName("DeviceSerialNo"   ) var DeviceSerialNo   : String? = null,
    @SerializedName("applang"          ) var applang          : String? = null,
    @SerializedName("PoVendorHeaderId" ) var PoVendorHeaderId : Int?    = null,
    @SerializedName("PoVendorDetailId" ) var PoVendorDetailId : Int?    = null,
    @SerializedName("InvoiceNumber"    ) var InvoiceNumber    : String? = null,
    @SerializedName("receivedQty"      ) var receivedQty      : Int?    = null,
    @SerializedName("RejectedQty"      ) var RejectedQty      : Int?    = null
)
