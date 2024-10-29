package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class ReceiveVendor_SerializedBody (
    @SerializedName("UserID"           ) var UserID           : String?               = null,
    @SerializedName("DeviceSerialNo"   ) var DeviceSerialNo   : String?            = null,
    @SerializedName("applang"          ) var applang          : String?            = null,
    @SerializedName("PoVendorHeaderId" ) var PoVendorHeaderId : Int?               = null,
    @SerializedName("PoVendorDetailId" ) var PoVendorDetailId : Int?               = null,
    @SerializedName("InvoiceNumber"    ) var InvoiceNumber    : String?            = null,
    @SerializedName("serials"          ) var serials          : List<Serial> = arrayListOf()
)
