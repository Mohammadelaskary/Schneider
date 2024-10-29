package net.gbs.schneider.Ui.IntermediateWarehouse

import net.gbs.schneider.Base.BaseRepository
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayProductionWorkOrderBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturnProduction_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturnProduction_UnserializedBody

class IntermediateWarehouseRepository : BaseRepository() {

    suspend fun getWorkOrdersList(userId: String, deviceSerialNumber: String, appLang: String) =
        apiInterface.getProductionWorkOrderList_Receiving(userId, deviceSerialNumber, appLang)

    suspend fun ReceiveProductionWorkOrder_Serialized(body: ReceiveProductionWorkOrder_SerializedBody) =
        apiInterface.ReceiveProductionWorkOrder_Serialized(body)

    suspend fun ReceiveProductionWorkOrder_Unserialized(body: ReceiveProductionWorkOrder_UnserializedBody) =
        apiInterface.ReceiveProductionWorkOrder_Unserialized(body)

    suspend fun getPutAwayWorkOrdersList(
        userId: String,
        deviceSerialNumber: String,
        appLang: String
    ) = apiInterface.GetProductionWorkOrderList_PutAway(userId, deviceSerialNumber, appLang)

    suspend fun PutAwayProductionWorkOrder_Serialized(body: PutAwayProductionWorkOrder_SerializedBody) =
        apiInterface.PutAwayProductionWorkOrder_Serialized(body)

    suspend fun PutAwayProductionWorkOrder_Unserialized(body: PutAwayProductionWorkOrder_UnserializedBody) =
        apiInterface.PutAwayProductionWorkOrder_Unserialized(body)

    suspend fun getIssueWorkOrdersList(
        userId: String,
        deviceSerialNumber: String,
        appLang: String
    ) = apiInterface.GetProductionWorkOrderList_Issue(userId, deviceSerialNumber, appLang)

    suspend fun IssueProductionWorkOrder_Serialized(body: IssueProductionWorkOrder_SerializedBody) =
        apiInterface.IssueProductionWorkOrder_Serialized(body)

    suspend fun IssueProductionWorkOrder_Unserialized(body: IssueProductionWorkOrder_UnserializedBody) =
        apiInterface.IssueProductionWorkOrder_Unserialized(body)

    suspend fun FastPutAwayProductionWorkOrder(body: FastPutAwayProductionWorkOrderBody) =
        apiInterface.FastPutAwayProductionWorkOrder(body)

    suspend fun GetReturnProductionList_Receiving(
        userId: String,
        deviceSerialNumber: String,
        appLang: String
    ) = apiInterface.GetReturnProductionList_Receiving(userId, deviceSerialNumber, appLang)

    suspend fun ReceiveReturnProduction_Serialized(body: ReceiveReturnProduction_SerializedBody) =
        apiInterface.ReceiveReturnProduction_Serialized(body)

    suspend fun ReceiveReturnProduction_Unserialized(body: ReceiveReturnProduction_UnserializedBody) =
        apiInterface.ReceiveReturnProduction_Unserialized(body)

    suspend fun GetReturnList_PutAway(
        userId: String,
        deviceSerialNumber: String,
        appLang: String
    ) = apiInterface.getReturnList_PutAway(userId, deviceSerialNumber, appLang)

    suspend fun PutAwayReturnProduction_Serialized(body: PutAwayReturnProduction_SerializedBody) =
        apiInterface.PutAwayReturnProduction_Serialized(body)

    suspend fun PutAwayReturnProduction_Unserialized(body: PutAwayReturnProduction_UnserializedBody) =
        apiInterface.PutAwayReturnProduction_Unserialized(body)

}