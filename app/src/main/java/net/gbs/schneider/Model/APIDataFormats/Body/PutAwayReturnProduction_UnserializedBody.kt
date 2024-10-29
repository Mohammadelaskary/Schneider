package net.gbs.schneider.Model.APIDataFormats.Body

import com.google.gson.annotations.SerializedName

class PutAwayReturnProduction_UnserializedBody (
    @SerializedName("userID"                   ) var userID                   : String?    = null,
    @SerializedName("deviceSerialNo"           ) var deviceSerialNo           : String? = null,
    @SerializedName("applang"                  ) var applang                  : String? = null,
    @SerializedName("workOrderReturnId"        ) var workOrderReturnId        : Int?    = null,
    @SerializedName("workOrderReturnDetailsId" ) var workOrderReturnDetailsId : Int?    = null,
    @SerializedName("storageBinCode"           ) var storageBinCode           : String? = null,
    @SerializedName("putAwayQty"               ) var putAwayQty               : Int?    = null

)