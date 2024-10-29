package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class PutAwayProductionWorkOrder_SerializedBody (
    @SerializedName("userID"                  ) var userID                  : String?               = null,
    @SerializedName("deviceSerialNo"          ) var deviceSerialNo          : String?            = null,
    @SerializedName("applang"                 ) var applang                 : String?            = null,
    @SerializedName("workOrderIssueId"        ) var workOrderIssueId        : Int?               = null,
    @SerializedName("workOrderIssueDetailsId" ) var workOrderIssueDetailsId : Int?               = null,
    @SerializedName("storageBinCode"          ) var storageBinCode          : String?            = null,
    @SerializedName("isBulk"                  ) var isBulk                  : Boolean?           = null,
    @SerializedName("bulkQty"                 ) var bulkQty                 : Int?               = null,
    @SerializedName("serials"                 ) var serials                 : List<Serial> = arrayListOf()
)
