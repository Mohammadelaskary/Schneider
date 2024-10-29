package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Invoice(
    @SerializedName("poPlantHeaderId"    ) var poPlantHeaderId    : Int,
    @SerializedName("poNumber"           ) var poNumber           : String,
    @SerializedName("projectNumber"      ) var projectNumber      : String,
    @SerializedName("salesOrderNumber"   ) var salesOrderNumber   : String,
    @SerializedName("poQty"              ) var poQty              : Int,
    @SerializedName("invoiceNumber"      ) var invoiceNumber      : String,
    @SerializedName("poStatus"           ) var poStatus           : String,
    @SerializedName("materials"          ) var materials          : List<Material>          = listOf(),
    @SerializedName("storagePermissions" ) var storagePermissions : MutableList<StoragePermission> = mutableListOf(),
) {
    companion object {
        fun toJson (invoice: Invoice):String {
            return Gson().toJson(invoice)
        }
        fun fromJson (invoice:String):Invoice {
            return Gson().fromJson(invoice,Invoice::class.java)
        }
    }
}