package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class IntermediateMaterialReturn (
    @SerializedName("workOrderReturnDetailsId" ) var workOrderReturnDetailsId : Int?                             = null,
    @SerializedName("materialId"               ) var materialId               : Int?                             = null,
    @SerializedName("materialCode"             ) var materialCode             : String?                          = null,
    @SerializedName("materialName"             ) var materialName             : String?                          = null,
    @SerializedName("returnedQuantity"         ) var returnedQuantity         : Int,
    @SerializedName("receivedQuantity"         ) var receivedQuantity         : Int,
    @SerializedName("putawayQuantity"          ) var putawayQuantity          : Int,
    @SerializedName("isSerialized"             ) var isSerialized             : Boolean,
    @SerializedName("isBulk"                   ) var isBulk                   : Boolean,
    @SerializedName("issueWorkOrderSerials"    ) var issueWorkOrderSerials    : List<ReturnSerial> = listOf(),
    @SerializedName("returnedSerials"          ) var returnedSerials          : List<Serial> = listOf()
) {
    val isSerializedWithOneSerial
        get() = isSerialized && isBulk
    val remainingQty
        get() = returnedQuantity - receivedQuantity
    val remainingPutAwayQty
        get() = receivedQuantity - putawayQuantity
}
