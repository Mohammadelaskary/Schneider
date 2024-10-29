package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AuditOrder(
    @SerializedName("auditHeaderId" ) var auditHeaderId : Int?     = null,
    @SerializedName("auditNo"       ) var auditNo       : String?  = null,
    @SerializedName("auditStatus"   ) var auditStatus   : String?  = null,
    @SerializedName("isClosed"      ) var isClosed      : Boolean? = null,
    @SerializedName("userIdClosed"  ) var userIdClosed  : String?  = null,
    @SerializedName("dateClosed"    ) var dateClosed    : String?  = null
){
    companion object{
        fun toJson (auditOrder: AuditOrder):String {
            return Gson().toJson(auditOrder)
        }
        fun fromJson (auditOrder:String):AuditOrder {
            return Gson().fromJson(auditOrder,AuditOrder::class.java)
        }
    }
}
