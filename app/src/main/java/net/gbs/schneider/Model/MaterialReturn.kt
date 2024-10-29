package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class MaterialReturn(

    @SerializedName("workOrderReturnDetailsId" ) var workOrderReturnDetailsId : Int?                             = null,
    @SerializedName("materialId"               ) var materialId               : Int?                             = null,
    @SerializedName("materialCode"             ) var materialCode             : String?                          = null,
    @SerializedName("materialName"             ) var materialName             : String?                          = null,
    @SerializedName("returnedQuantity"         ) var returnedQuantity         : Int,
    @SerializedName("receivedQuantity"         ) var receivedQuantity         : Int,
    @SerializedName("putawayQuantity"          ) var putawayQuantity          : Int,
    @SerializedName("isSerialized"             ) var isSerialized             : Boolean,
    @SerializedName("isBulk"                   ) var isBulk                   : Boolean,
    @SerializedName("issueWorkOrderSerials"    ) var issueWorkOrderSerials    : ArrayList<ReturnSerial> = arrayListOf(),
    @SerializedName("returnedSerials"          ) var returnedSerials          : ArrayList<ReturnSerial>                = arrayListOf()
){
    val isSerializedWithOneSerial:Boolean
        get() = isSerialized&&isBulk
    val remainingQty
        get() = returnedQuantity - receivedQuantity
    val remainingPutAwayQty
        get() = receivedQuantity - putawayQuantity

}
