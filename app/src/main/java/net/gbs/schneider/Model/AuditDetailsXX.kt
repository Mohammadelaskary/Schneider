package net.gbs.schneider.Model

data class AuditDetailsXX(
    val country: String,
    val isSerialized: Boolean,
    val materialCode: String,
    val materialId: Int,
    val materialName: String,
    val plantCode: String,
    val plantId: Int,
    val plantName: String,
    val projectNumber: String,
    val qty: Int,
    val serials: List<String>,
    val space: String,
    val storageBinCode: String,
    val storageBinId: Int,
    val storageBinName: String,
    val storageLocationCode: String,
    val storageLocationId: Int,
    val storageLocationName: String,
    val storageSectionCode: String,
    val storageSectionId: Int,
    val storageSectionName: String,
    val warehouseCode: String,
    val warehouseId: Int,
    val warehouseName: String
)