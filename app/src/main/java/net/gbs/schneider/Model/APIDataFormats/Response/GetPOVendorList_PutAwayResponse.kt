package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.APIDataFormats.InvoiceVendor

class GetPOVendorList_PutAwayResponse(
    @SerializedName("getList") val poVendorList: List<InvoiceVendor>
): BaseResponse<List<InvoiceVendor>>() {
    override fun getData(): List<InvoiceVendor> {
        return poVendorList
    }

}
