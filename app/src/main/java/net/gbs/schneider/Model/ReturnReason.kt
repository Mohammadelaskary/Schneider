package net.gbs.schneider.Model

data class ReturnReason(
    val returnReasonId: Int,
    val returnReasonText: String
){
    override fun toString(): String {
        return returnReasonText
    }
}