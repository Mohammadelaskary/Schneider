package net.gbs.schneider.Model.APIDataFormats.Body

data class PutAwayInvoice_UnserializedBody(
    var DeviceSerialNo: String = "",
    val PoPlantDetailId: Int,
    val PoPlantHeaderId: Int,
    val PutAwayQty: Int,
    val StorageBinCode: String,
    var UserID: String = "",
    var applang: String = ""
)