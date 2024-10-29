package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

class Bin (
    @SerializedName("storageBinId"        ) var storageBinId        : Int?    = null,
    @SerializedName("storageBinCode"      ) var storageBinCode      : String? = null,
    @SerializedName("storageSectionId"    ) var storageSectionId    : Int?    = null,
    @SerializedName("storageSectionCode"  ) var storageSectionCode  : String? = null,
    @SerializedName("storageLocationId"   ) var storageLocationId   : Int?    = null,
    @SerializedName("storageLocationCode" ) var storageLocationCode : String? = null,
    @SerializedName("storageLocationName" ) var storageLocationName : String? = null,
    @SerializedName("warehouseId"         ) var warehouseId         : Int?    = null,
    @SerializedName("warehouseCode"       ) var warehouseCode       : String? = null,
    @SerializedName("warehouseName"       ) var warehouseName       : String? = null,
    @SerializedName("space"               ) var space               : String? = null,
    @SerializedName("plantId"             ) var plantId             : Int?    = null,
    @SerializedName("plantCode"           ) var plantCode           : String? = null,
    @SerializedName("plantName"           ) var plantName           : String? = null,
    @SerializedName("country"             ) var country             : String? = null,
    @SerializedName("userIdAdd"           ) var userIdAdd           : Int?    = null,
    @SerializedName("userNameAdd"         ) var userNameAdd         : String? = null,
    @SerializedName("dateAdd"             ) var dateAdd             : String? = null,
    @SerializedName("userIdUpdate"        ) var userIdUpdate        : String? = null,
    @SerializedName("userNameUpdate"      ) var userNameUpdate      : String? = null,
    @SerializedName("dateUpdate"          ) var dateUpdate          : String? = null
)