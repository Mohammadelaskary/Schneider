package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.AuditDetails

class SaveAuditDetailsResponse(
    @SerializedName("getList") val auditDetailsList: List<AuditDetails>
): BaseResponse<List<AuditDetails>>() {
    override fun getData(): List<AuditDetails> {
        return auditDetailsList
    }
}
