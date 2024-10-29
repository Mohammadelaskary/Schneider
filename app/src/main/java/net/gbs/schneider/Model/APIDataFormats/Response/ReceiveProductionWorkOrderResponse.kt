package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse

class ReceiveProductionWorkOrderResponse (
    @SerializedName("productionWorkOrder") val intermediateWorkOrder: IntermediateWorkOrder
):BaseResponse<IntermediateWorkOrder>(){
    override fun getData(): IntermediateWorkOrder {
        return intermediateWorkOrder
    }

}
