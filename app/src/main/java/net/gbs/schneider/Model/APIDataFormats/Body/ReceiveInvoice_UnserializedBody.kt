package net.gbs.schneider.Model.APIDataFormats.Body

data class ReceiveInvoice_UnserializedBody(
    var DeviceSerialNo: String? = "",
    val PoPlantDetailId: Int,
    val PoPlantHeaderId: Int,
    val RejectedQty: Int,
    var UserID: String? = "",
    var applang: String? = "",
    val receivedQty: Int,
    val RejectionReasonId: Int?,
)