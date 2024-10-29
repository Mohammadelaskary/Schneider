package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.IntermediateWorkOrderReturn
import net.gbs.schneider.Model.WorkOrderReturn

class GetReturnProductionListResponse(
    @SerializedName("getList")val workOrdersList: List<IntermediateWorkOrderReturn>
): BaseResponse<List<IntermediateWorkOrderReturn>>() {
    override fun getData(): List<IntermediateWorkOrderReturn> {
        return workOrdersList
    }
}
