package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Invoice

class GetPOInvoiceList_ReceivingResponse(
    @SerializedName("getList")
    val invoiceList: List<Invoice>
                                         ): BaseResponse<List<Invoice>>() {
    override fun getData(): List<Invoice> {
        return invoiceList
    }

}
