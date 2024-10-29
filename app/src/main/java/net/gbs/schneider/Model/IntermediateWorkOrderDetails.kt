package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class IntermediateWorkOrderDetails (
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int?              = null,
    @SerializedName("projectNumberFrom"       ) var projectNumberFrom       : String?           = null,
    @SerializedName("projectNumberTo"         ) var projectNumberTo         : String?           = null,
    @SerializedName("materialId"              ) var materialId              : Int?              = null,
    @SerializedName("materialCode"            ) var materialCode            : String?           = null,
    @SerializedName("materialName"            ) var materialName            : String?           = null,
    @SerializedName("isSerialized"            ) var isSerialized            : Boolean,
    @SerializedName("isBulk"                  ) var isBulk                  : Boolean,
    @SerializedName("workOrderDetailQuantity" ) var workOrderDetailQuantity : Int?              = null,
    @SerializedName("issuedQuantity"          ) var issuedQuantity          : Int?              = null,
    @SerializedName("issuedSerials"           ) var issuedSerials           : ArrayList<Serial> = arrayListOf()
){
    val isSerializedWithOneSerial = isSerialized && isBulk
}