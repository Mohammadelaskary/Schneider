package net.gbs.schneider.Model.APIDataFormats.Body

data class ChangeQuantityRequest_POFactoryBody(
    val DeviceSerialNo: String,
    val PoPlantDetailId: Int,
    val PoPlantDetailQty: Int,
    val PoPlantHeaderId: Int,
    val UserID: String,
    val applang: String
)