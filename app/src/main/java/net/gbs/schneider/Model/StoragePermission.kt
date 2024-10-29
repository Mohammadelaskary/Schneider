package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class StoragePermission(
    @SerializedName("billOfLadingNo"   ) var billOfLadingNo   : String? = null,
    @SerializedName("billOfLadingDate" ) var billOfLadingDate : String? = null,
    @SerializedName("declarationNo"    ) var declarationNo    : String? = null,
    @SerializedName("declarationDate"  ) var declarationDate  : String? = null,
    @SerializedName("mrn"              ) var mrn              : String? = null
)
