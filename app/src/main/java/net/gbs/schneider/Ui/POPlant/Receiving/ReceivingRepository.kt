package net.gbs.schneider.Ui.POPlant.Receiving

import net.gbs.schneider.Base.BaseRepository
import net.gbs.schneider.Model.APIDataFormats.Body.ChangeQuantityRequest_POFactoryBody
import net.gbs.schneider.Model.APIDataFormats.Body.ChangeSerialRequest_POFactoryBody
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayInvoiceBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayInvoice_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayInvoice_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.SaveAuditDetailsBody

class ReceivingRepository : BaseRepository() {

    suspend fun getPOInvoiceList_Receiving(
        userId: String,
        deviceSerialNo: String,
        appLang: String,
        isPoVendor: Boolean
    ) = apiInterface.getPOInvoiceList_Receiving(userId, deviceSerialNo, appLang,isPoVendor)

    suspend fun ReceiveInvoice_Unserialized(body: ReceiveInvoice_UnserializedBody) =
        apiInterface.ReceiveInvoice_Unserialized(body)

    suspend fun ReceiveInvoice_Serialized(body: ReceiveInvoice_SerializedBody) =
        apiInterface.ReceiveInvoice_Serialized(body)

    suspend fun getPOInvoiceList_PutAway(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getPOInvoiceList_PutAway(userId, deviceSerialNo, appLang)

    suspend fun putAwayInvoice_Unserialized(body: PutAwayInvoice_UnserializedBody) =
        apiInterface.putAwayInvoice_Unserialized(body)

    suspend fun putAwayInvoice_Serialized(body: PutAwayInvoice_SerializedBody) =
        apiInterface.putAwayInvoice_Serialized(body)

    suspend fun GetBinCodeData(
        userId: String,
        deviceSerialNumber: String,
        appLang: String,
        binCode: String
    ) = apiInterface.getStorageBin_ByBinCode(userId, deviceSerialNumber, appLang, binCode)

    suspend fun GetReturnList_Receiving(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getReturnList_Receiving(userId, deviceSerialNo, appLang)

    suspend fun ReceiveReturn_Serialized(body: ReceiveReturn_SerializedBody) =
        apiInterface.ReceiveReturn_Serialized(body)

    suspend fun ReceiveReturn_Unserialized(body: ReceiveReturn_UnserializedBody) =
        apiInterface.ReceiveReturn_Unserialized(body)

    suspend fun getReturnProductionList_PutAway(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getReturnProductionList_PutAway(userId, deviceSerialNo, appLang)

    suspend fun PutAwayReturn_Serialized(body: PutAwayReturn_SerializedBody) =
        apiInterface.PutAwayReturn_Serialized(body)

    suspend fun PutAwayReturn_Unserialized(body: PutAwayReturn_UnserializedBody) =
        apiInterface.PutAwayReturn_Unserialized(body)

    suspend fun GetMaterialCodeData(
        userId: String,
        deviceSerialNumber: String,
        appLang: String,
        materialCode: String
    ) = apiInterface.getMaterialByCode(userId, deviceSerialNumber, appLang, materialCode)

    suspend fun checkSerialCode(
        userId: String,
        deviceSerialNumber: String,
        appLang: String,
        materialCode: String,
        serialNo: String
    ) = apiInterface.checkSerialRelatedToMaterial(
        userId,
        deviceSerialNumber,
        appLang,
        materialCode,
        serialNo
    )

    suspend fun saveAuditDetails(saveAuditBody: SaveAuditDetailsBody) =
        apiInterface.saveAuditDetails(saveAuditBody)

    suspend fun getPOVendorList_Receiving(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getPOVendorList_Receiving(userId, deviceSerialNo, appLang)

    suspend fun ReceiveVendor_Unserialized(body: ReceiveVendor_UnserializedBody) =
        apiInterface.ReceiveVendor_Unserialized(body)

    suspend fun ReceiveVendor_Serialized(body: ReceiveVendor_SerializedBody) =
        apiInterface.ReceiveVendor_Serialized(body)

    suspend fun getPOVendorList_PutAway(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getPOVendorList_PutAway(userId, deviceSerialNo, appLang)

    suspend fun putAwayVendor_Unserialized(body: PutAwayVendor_UnserializedBody) =
        apiInterface.putAwayVendor_Unserialized(body)

    suspend fun putAwayVendor_Serialized(body: PutAwayVendor_SerializedBody) =
        apiInterface.putAwayVendor_Serialized(body)

    suspend fun getReturnReasonList(userId: String, deviceSerialNumber: String, appLang: String) =
        apiInterface.getReturnReasonList(userId, deviceSerialNumber, appLang)

    suspend fun getRejectionReasons(userId: String, deviceSerialNumber: String, appLang: String) =
        apiInterface.getRejectionReasonList(userId, deviceSerialNumber, appLang)

    suspend fun changeSerials(body: ChangeSerialRequest_POFactoryBody) =
        apiInterface.ChangeSerialRequest_POFactory(body)

    suspend fun changeQty(body: ChangeQuantityRequest_POFactoryBody) =
        apiInterface.ChangeQuantityRequest_POFactory(body)

    suspend fun fastPutAway(body: FastPutAwayInvoiceBody) = apiInterface.FastPutAwayInvoice(body)

}