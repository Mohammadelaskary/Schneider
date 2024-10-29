package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.AuditOrder

class GetAuditHeaderResponse(
    @SerializedName("getList") val auditOrders:List<AuditOrder>
): BaseResponse<List<AuditOrder>>() {
    override fun getData(): List<AuditOrder> {
        return auditOrders
    }
}
