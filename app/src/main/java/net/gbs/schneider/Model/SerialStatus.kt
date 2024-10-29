package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

enum class SerialStatus(val status:Int) {
    @SerializedName("2")
    NEW(2),
    @SerializedName("1")
    REMOVED(1),
    @SerializedName("0")
    ORIGINAL(0)
}