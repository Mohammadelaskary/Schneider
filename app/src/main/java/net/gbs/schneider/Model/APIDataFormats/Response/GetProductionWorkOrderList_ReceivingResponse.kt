package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.RejectionReason
import net.gbs.schneider.Model.WorkOrder

class GetProductionWorkOrderList_ReceivingResponse(
    @SerializedName("getList") val workOrdersList: List<IntermediateWorkOrder>
) :BaseResponse<List<IntermediateWorkOrder>>() {
    override fun getData(): List<IntermediateWorkOrder> {
        return workOrdersList
    }
}