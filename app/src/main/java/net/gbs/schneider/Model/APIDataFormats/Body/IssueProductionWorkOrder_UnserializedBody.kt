package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

data class IssueProductionWorkOrder_UnserializedBody (
    @SerializedName("userID"                  ) var userID                  : String?    = null,
    @SerializedName("deviceSerialNo"          ) var deviceSerialNo          : String? = null,
    @SerializedName("applang"                 ) var applang                 : String? = null,
    @SerializedName("workOrderIssueId"        ) var workOrderIssueId        : Int?    = null,
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int?    = null,
    @SerializedName("issuedQty"               ) var issuedQty               : Int?    = null,
    @SerializedName("storageBinCode"          ) var storageBinCode          : String? = null
)