package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.MaterialData

class GetMaterialByCodeResponse(
    @SerializedName("getData") val materialData: MaterialData
): BaseResponse<MaterialData>() {
    override fun getData(): MaterialData {
        return materialData
    }
}