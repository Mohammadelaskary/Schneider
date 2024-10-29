package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.WorkOrderReturn
import retrofit2.Response

class GetReturnList_PutAwayResponse(
    @SerializedName("getList") val   workOrderList:List<WorkOrderReturn>
): BaseResponse<List<WorkOrderReturn>>() {
    override fun getData(): List<WorkOrderReturn> {
        return workOrderList
    }

}
