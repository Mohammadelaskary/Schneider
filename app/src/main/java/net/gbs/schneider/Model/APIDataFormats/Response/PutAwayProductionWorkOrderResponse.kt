package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Serial

data class PutAwayProductionWorkOrderResponse (
    @SerializedName("workOrderProduction") val workOrder: IntermediateWorkOrder
) : BaseResponse<IntermediateWorkOrder>() {
    override fun getData(): IntermediateWorkOrder {
        return workOrder
    }
}