package net.gbs.schneider.Model

data class Material(
    val acceptedQty: Int,
    val boxNumber: String,
    val isSerialized: Boolean,
    val isBulk: Boolean,
    val lineNumber: Int,
    val materialCode: String,
    val materialId: Int,
    val materialName: String,
    val poPlantDetailId: Int,
    val putawayQty: Int,
    val rejectedQty: Int,
    val serials: List<Serial>,
    val shippedQuantity: Int,
    val supplyPlant: String
) {
    val receivedQty:Int
        get() = acceptedQty + rejectedQty
    val isSerializedWithOneSerial:Boolean
        get() = isSerialized&&isBulk
    val remainingQty :Int
        get() = shippedQuantity - acceptedQty - rejectedQty
    val remainingQtyPutAway: Int
        get() = acceptedQty - putawayQty
    fun serialsText():String {
        var serialsText = ""
        serials.forEachIndexed { index, serial ->
            serialsText += if (index<serials.size-1){
                "${serial.serial}, "
            } else {
                serial.serial
            }
        }
        return serialsText
    }
    var isSelected = false
}