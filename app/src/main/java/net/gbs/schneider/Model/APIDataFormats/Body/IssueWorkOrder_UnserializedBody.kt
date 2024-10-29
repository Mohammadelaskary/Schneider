package net.gbs.schneider.Model.APIDataFormats.Body

data class IssueWorkOrder_UnserializedBody(
    var DeviceSerialNo: String = "",
    var UserID: String = "",
    val WorkOrderIssueDetailsId: Int,
    val WorkOrderIssueId: Int,
    var applang: String = "",
    val issuedQty: Int,
    val StorageBinCode : String,
)