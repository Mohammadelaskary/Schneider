package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor

class ReceiveVendor_SerializedResponse(
    @SerializedName("invoice") val invoiceVendor: InvoiceVendor
): BaseResponse<InvoiceVendor>() {
    override fun getData(): InvoiceVendor {
        return invoiceVendor
    }

}
