package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.Serial

data class IssueWorkOrder_SerializedBody(
    @SerializedName("UserID"                  ) var UserID                  : String = "",
    @SerializedName("DeviceSerialNo"          ) var DeviceSerialNo          : String = "",
    @SerializedName("applang"                 ) var applang                 : String = "",
    @SerializedName("WorkOrderIssueId"        ) var WorkOrderIssueId        : Int?               = null,
    @SerializedName("WorkOrderIssueDetailsId" ) var WorkOrderIssueDetailsId : Int?               = null,
    @SerializedName("IsBulk"                  ) var IsBulk                  : Boolean?           = null,
    @SerializedName("BulkQty"                 ) var BulkQty                 : Int?               = null,
    @SerializedName("serials"                 ) var serials                 : List<Serial> = arrayListOf()

)