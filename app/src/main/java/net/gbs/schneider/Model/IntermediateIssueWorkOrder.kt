package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.APIDataFormats.Response.IntermediateWorkOrder

data class IntermediateIssueWorkOrder(

    @SerializedName("workOrderIssueId"             ) var workOrderIssueId             : Int?                        = null,
    @SerializedName("workOrderNumber"              ) var workOrderNumber              : String?                     = null,
    @SerializedName("workOrderDate"                ) var workOrderDate                : String?                     = null,
    @SerializedName("projectNumberTo"              ) var projectNumberTo              : String?                     = null,
    @SerializedName("isApproved"                   ) var isApproved                   : Boolean?                    = null,
    @SerializedName("workOrderDetails"             ) var workOrderDetails             : ArrayList<IntermediateIssueWorkOrderDetails> = arrayListOf(),
    @SerializedName("productionSparepartWorkOrder" ) var productionSparepartWorkOrder : Int?                        = null
) {
    val workOrderType:WorkOrderType
        get() = if (productionSparepartWorkOrder == 1){
            WorkOrderType.PRODUCTION
        } else {
            WorkOrderType.SPARE_PARTS
        }
    companion object {
        fun toJson(workOrder: IntermediateIssueWorkOrder): String {
            return Gson().toJson(workOrder)
        }
        fun fromJson(workOrder:String): IntermediateIssueWorkOrder {
            return Gson().fromJson(workOrder, IntermediateIssueWorkOrder::class.java)
        }
    }
}

