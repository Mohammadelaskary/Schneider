package net.gbs.schneider.Model

data class VendorMaterial(
    val acceptedQty: Int,
    val isSerialized: Boolean,
    val lineNumber: Int,
    val materialCode: String,
    val materialId: Int,
    val materialName: String,
    val poVendorDetailId: Int,
    val putawayQty: Any,
    val rejectedQty: Int,
    val serials: List<Serial>,
    val shippedQuantity: Int
)