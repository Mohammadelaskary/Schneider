package net.gbs.schneider.Model.APIDataFormats.Body

data class PutAwayReturn_UnserializedBody(
    var DeviceSerialNo: String="",
    val PutAwayQty: Int,
    val StorageBinCode: String,
    var UserID: String = "",
    val WorkOrderReturnDetailsId: Int,
    val WorkOrderReturnId: Int,
    var applang: String = ""
)