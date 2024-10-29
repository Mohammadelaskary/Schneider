package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

class FastPutAwayProductionWorkOrderBody (
    @SerializedName("userID"                  ) var userID                  : String?           = null,
    @SerializedName("deviceSerialNo"          ) var deviceSerialNo          : String?        = null,
    @SerializedName("applang"                 ) var applang                 : String?        = null,
    @SerializedName("workOrderIssueId"        ) var workOrderIssueId        : Int?           = null,
    @SerializedName("storageBinCode"          ) var storageBinCode          : String?        = null,
    @SerializedName("isFullPutaway"           ) var isFullPutaway           : Boolean?       = null,
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : List<Int> = listOf()
)