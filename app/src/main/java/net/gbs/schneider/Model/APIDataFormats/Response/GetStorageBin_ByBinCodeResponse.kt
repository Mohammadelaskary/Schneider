package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Bin

class GetStorageBin_ByBinCodeResponse(
    @SerializedName("getList") var binList: List<Bin>
) : BaseResponse<List<Bin>>() {
    override fun getData(): List<Bin> {
        return binList
    }

}
