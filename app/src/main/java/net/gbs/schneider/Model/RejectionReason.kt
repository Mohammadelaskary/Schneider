package net.gbs.schneider.Model

import com.google.gson.annotations.SerializedName

data class RejectionReason(
    @SerializedName("rejectionReasonId"   ) var rejectionReasonId   : Int?    = null,
    @SerializedName("rejectionReasonText" ) var rejectionReasonText : String? = null
) {
    override fun toString(): String {
        return rejectionReasonText!!
    }
}
