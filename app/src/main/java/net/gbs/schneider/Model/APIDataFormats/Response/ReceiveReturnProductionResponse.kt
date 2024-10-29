package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.IntermediateIssueWorkOrder
import net.gbs.schneider.Model.IntermediateWorkOrderReturn

class ReceiveReturnProductionResponse (@SerializedName("workOrderReturn") val workOrder: IntermediateWorkOrderReturn
) : BaseResponse<IntermediateWorkOrderReturn>() {
    override fun getData(): IntermediateWorkOrderReturn {
        return workOrder
    }
}
