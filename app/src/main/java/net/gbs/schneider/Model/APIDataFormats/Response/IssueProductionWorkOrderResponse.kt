package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.IntermediateIssueWorkOrder

data class IssueProductionWorkOrderResponse (
    @SerializedName("workorder") val workOrder: IntermediateIssueWorkOrder
) : BaseResponse<IntermediateIssueWorkOrder>() {
    override fun getData(): IntermediateIssueWorkOrder {
        return workOrder
    }
}
