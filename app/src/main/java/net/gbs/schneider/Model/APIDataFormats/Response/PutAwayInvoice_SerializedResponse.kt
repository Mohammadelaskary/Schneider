package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Invoice

class PutAwayInvoice_SerializedResponse(
    @SerializedName("invoice") val invoice: Invoice
) : BaseResponse<Invoice>() {
    override fun getData(): Invoice {
        return invoice
    }

}
