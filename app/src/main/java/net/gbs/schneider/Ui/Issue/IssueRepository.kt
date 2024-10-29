package net.gbs.schneider.Ui.Issue

import net.gbs.schneider.Base.BaseRepository
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_UnserializedBody

class IssueRepository : BaseRepository() {
    suspend fun GetWorkOrderList_Issue(
        userId: String,
        deviceSerialNumber: String,
        appLang: String
    ) = apiInterface.getWorkOrderList_Issue(userId, deviceSerialNumber, appLang)

    suspend fun issueWorkOrder_Serialized(issueworkorderSerializedbody: IssueWorkOrder_SerializedBody) =
        apiInterface.issueWorkOrder_Serialized(issueworkorderSerializedbody)

    suspend fun issueWorkOrder_Unserialized(issueworkorderUnserializedbody: IssueWorkOrder_UnserializedBody) =
        apiInterface.issueWorkOrder_Unserialized(issueworkorderUnserializedbody)

    suspend fun GetBinCodeData(
        userId: String,
        deviceSerialNumber: String,
        appLang: String,
        binCode: String
    ) = apiInterface.getStorageBin_ByBinCode(userId, deviceSerialNumber, appLang, binCode)
}