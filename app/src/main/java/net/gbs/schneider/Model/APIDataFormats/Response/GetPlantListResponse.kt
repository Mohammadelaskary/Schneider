package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Plant

class GetPlantListResponse(@SerializedName("getList")private val plantList: List<Plant>): BaseResponse<List<Plant>>() {
    override fun getData(): List<Plant> {
        return plantList
    }
}