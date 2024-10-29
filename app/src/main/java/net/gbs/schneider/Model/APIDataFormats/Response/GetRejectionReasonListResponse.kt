package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.RejectionReason

class GetRejectionReasonListResponse(
    @SerializedName("getList") val rejectionReasonsList: List<RejectionReason>
) : BaseResponse<List<RejectionReason>>() {
    override fun getData(): List<RejectionReason> {
        return rejectionReasonsList
    }

}
