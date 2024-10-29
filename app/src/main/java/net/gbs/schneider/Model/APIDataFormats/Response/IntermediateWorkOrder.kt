package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.APIDataFormats.IntermediateMaterial
import net.gbs.schneider.Model.WorkOrder

class IntermediateWorkOrder (
    @SerializedName("workOrderIssueId" ) var workOrderIssueId : Int?                 = null,
    @SerializedName("workOrderNumber"  ) var workOrderNumber  : String?              = null,
    @SerializedName("projectNumber"    ) var projectNumber    : String?              = null,
    @SerializedName("projectNumberTo"  ) var projectNumberTo  : String?              = null,
    @SerializedName("materials"        ) var materials        : ArrayList<IntermediateMaterial> = arrayListOf()
){
    companion object {
        fun toJson(workOrder: IntermediateWorkOrder): String {
            return Gson().toJson(workOrder)
        }
        fun fromJson(workOrder:String): IntermediateWorkOrder {
            return Gson().fromJson(workOrder, IntermediateWorkOrder::class.java)
        }
    }
}
