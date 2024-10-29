package net.gbs.schneider.Model

data class AuditDetails(
    val auditDetailsList: List<AuditDetailsXX>,
    val auditHeaderId: Int,
    val auditHeader_IsClosed: Boolean,
    val auditNo: String,
    val auditStatus: String
)