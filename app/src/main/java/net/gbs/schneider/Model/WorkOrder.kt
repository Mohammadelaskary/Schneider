package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class WorkOrder (

    @SerializedName("workOrderIssueId" ) var workOrderIssueId : Int?                        = null,
    @SerializedName("workOrderNumber"  ) var workOrderNumber  : String="",
    @SerializedName("workOrderDate"    ) var workOrderDate    : String?                     = null,
    @SerializedName("projectNumber"    ) var projectNumber    : String="",
    @SerializedName("projectNumberTo"  ) var projectNumberTo  : String="",
    @SerializedName("isApproved"       ) var isApproved       : Boolean?                    = null,
    @SerializedName("workOrderDetails" ) var workOrderDetails : ArrayList<WorkOrderDetails> = arrayListOf(),
    @SerializedName("productionSparepartWorkOrder" ) var productionSparePartWorkOrder : Int,

) {
    val workOrderType:WorkOrderType
        get() = if (productionSparePartWorkOrder == 1){
            WorkOrderType.PRODUCTION
        } else {
            WorkOrderType.SPARE_PARTS
        }
    companion object {
        fun toJson(workOrder: WorkOrder): String {
            return Gson().toJson(workOrder)
        }
        fun fromJson(workOrder:String):WorkOrder {
            return Gson().fromJson(workOrder,WorkOrder::class.java)
        }
    }

}