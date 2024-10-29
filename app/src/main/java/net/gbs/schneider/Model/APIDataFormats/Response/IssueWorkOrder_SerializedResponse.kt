package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.WorkOrder

class IssueWorkOrder_SerializedResponse(
    @SerializedName("workorder") val workOrder: WorkOrder
    ) : BaseResponse<WorkOrder>() {
    override fun getData(): WorkOrder {
        return workOrder
    }

}
