package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.WorkOrderReturn

class GetReturnList_ReceivingResponse(
    @SerializedName("getList")val invoiceList: List<WorkOrderReturn>
    ): BaseResponse<List<WorkOrderReturn>>() {
    override fun getData(): List<WorkOrderReturn> {
        return invoiceList
    }
}
