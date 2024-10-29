package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class ReceiveInvoice_SerializedBody(
    var DeviceSerialNo: String? = "",
    val PoPlantDetailId: Int,
    val PoPlantHeaderId: Int,
    var UserID: String? = "",
    var applang: String? = "",
    val serials: List<Serial>,
    @SerializedName("RejectionReasonId" ) var RejectionReasonId : Int?               = null,
    @SerializedName("IsBulk"            ) var IsBulk            : Boolean?           = null,
    @SerializedName("BulkQty"           ) var BulkQty           : Int?               = null,
    @SerializedName("RejectedQty"       ) var RejectedQty           : Int?               = null,
)