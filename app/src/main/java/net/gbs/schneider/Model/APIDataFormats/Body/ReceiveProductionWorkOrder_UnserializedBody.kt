package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

class ReceiveProductionWorkOrder_UnserializedBody (
    @SerializedName("userID"                  ) var userID                  : String?    = null,
    @SerializedName("deviceSerialNo"          ) var deviceSerialNo          : String? = null,
    @SerializedName("applang"                 ) var applang                 : String? = null,
    @SerializedName("workOrderIssueId"        ) var workOrderIssueId        : Int?    = null,
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int?    = null,
    @SerializedName("receivedQty"             ) var receivedQty             : Int?    = null
)