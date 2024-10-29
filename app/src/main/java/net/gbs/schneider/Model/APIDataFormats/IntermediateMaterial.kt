package net.gbs.schneider.Model.APIDataFormats

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

class IntermediateMaterial (
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int,
    @SerializedName("materialId"              ) var materialId              : Int?              = null,
    @SerializedName("materialCode"            ) var materialCode            : String?           = null,
    @SerializedName("materialName"            ) var materialName            : String?           = null,
    @SerializedName("issuedQuantity"          ) var issuedQuantity          : Int,
    @SerializedName("receivedQuantity"        ) var receivedQuantity        : Int,
    @SerializedName("isSerialized"            ) var isSerialized            : Boolean,
    @SerializedName("isBulk"                  ) var isBulk                  : Boolean,
    @SerializedName("putawayQty"              ) var putawayQty              : Int,
    @SerializedName("serials"                 ) var serials                 : ArrayList<Serial> = arrayListOf()
) {
    val isSerializedWithOneSerial:Boolean
        get() = isSerialized&&isBulk
    val remainingQty :Int
        get() = issuedQuantity - receivedQuantity
    val remainingQtyPutAway: Int
        get() = receivedQuantity - putawayQty
    var isSelected = false
}