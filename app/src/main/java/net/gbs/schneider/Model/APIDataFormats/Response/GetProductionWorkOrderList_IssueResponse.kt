package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.IntermediateIssueWorkOrder

data class GetProductionWorkOrderList_IssueResponse (
    @SerializedName("getList") val workOrdersList: List<IntermediateIssueWorkOrder>
) : BaseResponse<List<IntermediateIssueWorkOrder>>() {
    override fun getData(): List<IntermediateIssueWorkOrder> {
        return workOrdersList
    }
}
