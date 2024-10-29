package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Stock

class GetItemInfoResponse(
    @SerializedName("getList"        ) var getList        : ArrayList<Stock> = arrayListOf()
) : BaseResponse<List<Stock>>() {
    override fun getData(): List<Stock> {
        return getList
    }

}
