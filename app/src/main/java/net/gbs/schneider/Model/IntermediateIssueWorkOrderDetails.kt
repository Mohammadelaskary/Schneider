package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class IntermediateIssueWorkOrderDetails (
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int?              = null,
    @SerializedName("projectNumberFrom"       ) var projectNumberFrom       : String?           = null,
    @SerializedName("projectNumberTo"         ) var projectNumberTo         : String?           = null,
    @SerializedName("materialId"              ) var materialId              : Int?              = null,
    @SerializedName("materialCode"            ) var materialCode            : String?           = null,
    @SerializedName("materialName"            ) var materialName            : String?           = null,
    @SerializedName("isSerialized"            ) var isSerialized            : Boolean,
    @SerializedName("isBulk"                  ) var isBulk                  : Boolean,
    @SerializedName("workOrderDetailQuantity" ) var workOrderDetailQuantity : Int,
    @SerializedName("issuedQuantity"          ) var issuedQuantity          : Int,
    @SerializedName("issuedSerials"           ) var issuedSerials           : List<Serial> = listOf()
){
    val isSerializedWithOneSerial
        get() = isSerialized && isBulk
    val remainingQty
        get() = workOrderDetailQuantity - issuedQuantity
}