package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.ReturnReason

class GetReturnReasonListResponse(
    @SerializedName("getList") val reasonList: List<ReturnReason>
): BaseResponse<List<ReturnReason>>() {
    override fun getData(): List<ReturnReason> {
        return reasonList
    }
}
