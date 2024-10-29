package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class MaterialData(
    @SerializedName("materialId"     ) var materialId     : Int?     = null,
    @SerializedName("materialCode"   ) var materialCode   : String?  = null,
    @SerializedName("materialName"   ) var materialName   : String?  = null,
    @SerializedName("uom"            ) var uom            : String?  = null,
    @SerializedName("materialType"   ) var materialType   : String?  = null,
    @SerializedName("isSerialized"   ) var isSerialized   : Boolean? = null,
    @SerializedName("userIdAdd"      ) var userIdAdd      : Int?     = null,
    @SerializedName("userNameAdd"    ) var userNameAdd    : String?  = null,
    @SerializedName("dateAdd"        ) var dateAdd        : String?  = null,
    @SerializedName("userIdUpdate"   ) var userIdUpdate   : String?  = null,
    @SerializedName("userNameUpdate" ) var userNameUpdate : String?  = null,
    @SerializedName("dateUpdate"     ) var dateUpdate     : String?  = null
)
