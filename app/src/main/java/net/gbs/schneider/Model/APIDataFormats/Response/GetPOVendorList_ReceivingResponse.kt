package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor
import net.gbs.schneider.Model.VendorMaterial

class GetPOVendorList_ReceivingResponse (
    @SerializedName("getList") val invoiceVendorList: List<InvoiceVendor>
) : BaseResponse<List<InvoiceVendor>>() {
    override fun getData(): List<InvoiceVendor> {
        return invoiceVendorList
    }
}
