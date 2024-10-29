package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.WorkOrder

class GetWorkOrderList_IssueResponse(
    @SerializedName("getList"        ) var getList        : ArrayList<WorkOrder>
    ): BaseResponse<List<WorkOrder>>() {
    override fun getData(): List<WorkOrder> {
        return getList
    }

}
