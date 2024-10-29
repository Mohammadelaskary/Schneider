package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse

class GetProductionWorkOrderList_PutAwayResponse(
    @SerializedName("getList") val workOrdersList: List<IntermediateWorkOrder>
) : BaseResponse<List<IntermediateWorkOrder>>() {
    override fun getData(): List<IntermediateWorkOrder> {
        return workOrdersList
    }
}