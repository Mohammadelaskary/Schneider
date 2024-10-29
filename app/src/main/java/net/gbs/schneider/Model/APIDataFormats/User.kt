package net.gbs.schneider.Model.APIDataFormats

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("userId"       ) var userId       : Int?     = null,
    @SerializedName("userName"     ) var userName     : String?  = null,
    @SerializedName("password"     ) var password     : String?  = null,
    @SerializedName("isMobileUser" ) var isMobileUser : Boolean? = null,
    @SerializedName("isReceive"    ) var isReceive    : Boolean? = null,
    @SerializedName("isIssue"      ) var isIssue      : Boolean? = null,
    @SerializedName("isReturn"     ) var isReturn     : Boolean? = null,
    @SerializedName("isAudit"      ) var isAudit      : Boolean? = null,
    @SerializedName("isPutAway"    ) var isPutAway    : Boolean? = null,
    @SerializedName("isActive"     ) var isActive     : Boolean? = null,
    @SerializedName("isPrintingApp" ) var isPrintingApp : Boolean? = null,
    @SerializedName("scanMode"      ) var scanMode      : Int?     = null,
    @SerializedName("qtyMode"       ) var qtyMode       : Int?     = null,
){
    val canEnterQty:Boolean get() = qtyMode== 1
    val canSelectMaterial:Boolean get() = scanMode == 2
}