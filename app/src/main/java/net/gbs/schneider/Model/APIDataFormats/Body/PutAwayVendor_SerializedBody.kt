package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

class PutAwayVendor_SerializedBody (
    @SerializedName("UserID"           ) var UserID           : String?               = null,
    @SerializedName("DeviceSerialNo"   ) var DeviceSerialNo   : String?            = null,
    @SerializedName("applang"          ) var applang          : String?            = null,
    @SerializedName("PoVendorHeaderId" ) var PoVendorHeaderId : Int?               = null,
    @SerializedName("PoVendorDetailId" ) var PoVendorDetailId : Int?               = null,
    @SerializedName("StorageBinCode"   ) var StorageBinCode   : String?            = null,
    @SerializedName("serials"          ) var serials          : List<Serial> = arrayListOf()
)
