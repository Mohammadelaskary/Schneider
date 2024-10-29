package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.WorkOrderReturn

class PutAwayReturnResponse(
    @SerializedName("workOrderReturn") val workOrderReturn: WorkOrderReturn
) : BaseResponse<WorkOrderReturn>() {
    override fun getData(): WorkOrderReturn {
        return workOrderReturn
    }
}
