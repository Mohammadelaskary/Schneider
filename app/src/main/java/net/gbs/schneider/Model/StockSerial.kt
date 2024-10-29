package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class StockSerial(
    @SerializedName("serial" ) var serial : String? = null

)
