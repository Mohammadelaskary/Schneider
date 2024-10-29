package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class Stock(
    @SerializedName("stockId"             ) var stockId             : Int?                    = null,
    @SerializedName("materialId"          ) var materialId          : Int?                    = null,
    @SerializedName("plantId"             ) var plantId             : Int?                    = null,
    @SerializedName("warehouseId"         ) var warehouseId         : Int?                    = null,
    @SerializedName("storageLocationId"   ) var storageLocationId   : Int?                    = null,
    @SerializedName("storageSectionId"    ) var storageSectionId    : Int?                    = null,
    @SerializedName("storageBinId"        ) var storageBinId        : Int?                    = null,
    @SerializedName("projectNumber"       ) var projectNumber       : String?                 = null,
    @SerializedName("plantCode"           ) var plantCode           : String?                 = null,
    @SerializedName("plantName"           ) var plantName           : String?                 = null,
    @SerializedName("warehouseCode"       ) var warehouseCode       : String?                 = null,
    @SerializedName("warehouseName"       ) var warehouseName       : String?                 = null,
    @SerializedName("storageLocationCode" ) var storageLocationCode : String?                 = null,
    @SerializedName("storageLocationName" ) var storageLocationName : String?                 = null,
    @SerializedName("storageSectionCode"  ) var storageSectionCode  : String?                 = null,
    @SerializedName("storageBinCode"      ) var storageBinCode      : String?                 = null,
    @SerializedName("materialCode"        ) var materialCode        : String?                 = null,
    @SerializedName("materialName"        ) var materialName        : String?                 = null,
    @SerializedName("isSerialized"        ) var isSerialized        : Boolean?                = null,
    @SerializedName("stockQty"            ) var stockQty            : Int?                    = null,
    @SerializedName("stockSerials"        ) var stockSerials        : ArrayList<StockSerial> = arrayListOf()

)
