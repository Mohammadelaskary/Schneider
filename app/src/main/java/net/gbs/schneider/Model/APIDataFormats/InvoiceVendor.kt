package net.gbs.schneider.Model.APIDataFormats

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Model.VendorMaterial

data class InvoiceVendor(
    @SerializedName("poVendorHeaderId" ) var poVendorHeaderId : Int?                 = null,
    @SerializedName("poNumber"         ) var poNumber         : String?              = null,
    @SerializedName("projectNumber"    ) var projectNumber    : String?              = null,
    @SerializedName("supplyVendor"     ) var supplyVendor     : String?              = null,
    @SerializedName("poQty"            ) var poQty            : Int?                 = null,
    @SerializedName("poStatus"         ) var poStatus         : String?              = null,
    @SerializedName("materials"        ) var materials        : ArrayList<VendorMaterial> = arrayListOf()
) {
    companion object{
        fun toJson(invoiceVendor: InvoiceVendor):String{
            return Gson().toJson(invoiceVendor)
        }
        fun fromJson (invoiceVendor: String):InvoiceVendor{
            return Gson().fromJson(invoiceVendor,InvoiceVendor::class.java)
        }
    }
}
