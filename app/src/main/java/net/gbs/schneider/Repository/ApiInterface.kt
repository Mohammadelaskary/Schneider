package net.gbs.schneider.Repository

import net.gbs.schneider.Model.APIDataFormats.Body.ChangeQuantityRequest_POFactoryBody
import net.gbs.schneider.Model.APIDataFormats.Body.ChangeSerialRequest_POFactoryBody
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayInvoiceBody
import net.gbs.schneider.Model.APIDataFormats.Body.FastPutAwayProductionWorkOrderBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.IssueWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayInvoice_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayInvoice_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturnProduction_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayReturn_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.PutAwayVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveInvoice_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveProductionWorkOrder_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturnProduction_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturnProduction_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveReturn_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_SerializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.ReceiveVendor_UnserializedBody
import net.gbs.schneider.Model.APIDataFormats.Body.SaveAuditDetailsBody
import net.gbs.schneider.Model.APIDataFormats.Body.SignInBody
import net.gbs.schneider.Model.APIDataFormats.Response.FastPutAwayInvoiceResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetAuditHeaderResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetMaterialByCodeResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetPOInvoiceList_PutAwayResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetPOInvoiceList_ReceivingResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetPOVendorList_PutAwayResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetPOVendorList_ReceivingResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetPlantListResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetProductionWorkOrderList_IssueResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetProductionWorkOrderList_PutAwayResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetProductionWorkOrderList_ReceivingResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetRejectionReasonListResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetReturnList_PutAwayResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetReturnList_ReceivingResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetReturnProductionListResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetReturnReasonListResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetStockResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetStorageBin_ByBinCodeResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetWarehouseListResponse
import net.gbs.schneider.Model.APIDataFormats.Response.GetWorkOrderList_IssueResponse
import net.gbs.schneider.Model.APIDataFormats.Response.IssueProductionWorkOrderResponse
import net.gbs.schneider.Model.APIDataFormats.Response.IssueWorkOrder_SerializedResponse
import net.gbs.schneider.Model.APIDataFormats.Response.NoDataResponse
import net.gbs.schneider.Model.APIDataFormats.Response.PutAwayInvoice_SerializedResponse
import net.gbs.schneider.Model.APIDataFormats.Response.PutAwayProductionWorkOrderResponse
import net.gbs.schneider.Model.APIDataFormats.Response.PutAwayReturnProductionResponse
import net.gbs.schneider.Model.APIDataFormats.Response.PutAwayReturnResponse
import net.gbs.schneider.Model.APIDataFormats.Response.PutAwayVendor_SerializedResponse
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveInvoice_SerializedResponse
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveProductionWorkOrderResponse
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveReturnProductionResponse
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveReturnResponse
import net.gbs.schneider.Model.APIDataFormats.Response.ReceiveVendor_SerializedResponse
import net.gbs.schneider.Model.APIDataFormats.Response.SaveAuditDetailsResponse
import net.gbs.schneider.Model.APIDataFormats.Response.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiInterface {

    @POST("SignIn")
    suspend fun signIn(
        @Body signInData : SignInBody
    ) : Response<SignInResponse>

    @GET("GetPOInvoiceList_Receiving")
    suspend fun getPOInvoiceList_Receiving(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  applang : String,
        @Query("IsPoVendor")  isPoVendor : Boolean,
    ) : Response<GetPOInvoiceList_ReceivingResponse>

    @POST("ReceiveInvoice_Serialized")
    suspend fun ReceiveInvoice_Serialized(
        @Body  body : ReceiveInvoice_SerializedBody
    ) : Response<ReceiveInvoice_SerializedResponse>

    @POST("ReceiveInvoice_Unserialized")
    suspend fun ReceiveInvoice_Unserialized(
        @Body  body : ReceiveInvoice_UnserializedBody
    ) : Response<ReceiveInvoice_SerializedResponse>

    @GET("GetPlantList")
    suspend fun getPlantList(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetPlantListResponse>

    @GET("GetWarehouseList")
    suspend fun getWarehouseList(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("PlantId")  PlantId : Int,
    ) : Response<GetWarehouseListResponse>
    @GET("GetWorkOrderList_Issue")
    suspend fun getWorkOrderList_Issue(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetWorkOrderList_IssueResponse>

    @POST("IssueWorkOrder_Serialized")
    suspend fun issueWorkOrder_Serialized(
        @Body  body : IssueWorkOrder_SerializedBody
    ) : Response<IssueWorkOrder_SerializedResponse>
    @POST("IssueWorkOrder_Unserialized")
    suspend fun issueWorkOrder_Unserialized(
        @Body  body : IssueWorkOrder_UnserializedBody
    ) : Response<IssueWorkOrder_SerializedResponse>

    @GET("GetStorageBin_ByBinCode")
    suspend fun getStorageBin_ByBinCode(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("StorageBinCode")  StorageBinCode : String,
    ) : Response<GetStorageBin_ByBinCodeResponse>

    @GET("GetPOInvoiceList_PutAway")
    suspend fun getPOInvoiceList_PutAway(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetPOInvoiceList_PutAwayResponse>


    @POST("PutAwayInvoice_Serialized")
    suspend fun putAwayInvoice_Serialized(
        @Body  body : PutAwayInvoice_SerializedBody
    ) : Response<PutAwayInvoice_SerializedResponse>
    @POST("PutAwayInvoice_Unserialized")
    suspend fun putAwayInvoice_Unserialized(
        @Body  body : PutAwayInvoice_UnserializedBody
    ) : Response<PutAwayInvoice_SerializedResponse>
    @GET("GetPOVendorList_PutAway")
    suspend fun getPOVendorList_PutAway(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetPOVendorList_PutAwayResponse>
    @POST("PutAwayVendor_Serialized")
    suspend fun putAwayVendor_Serialized(
        @Body  body : PutAwayVendor_SerializedBody
    ) : Response<PutAwayVendor_SerializedResponse>
    @POST("PutAwayVendor_Unserialized")
    suspend fun putAwayVendor_Unserialized(
        @Body  body : PutAwayVendor_UnserializedBody
    ) : Response<PutAwayVendor_SerializedResponse>
    @GET("GetReturnList_Receiving_Main")
    suspend fun getReturnList_Receiving(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetReturnList_ReceivingResponse>
    @POST("ReceiveReturn_Serialized_Main")
    suspend fun ReceiveReturn_Serialized(
        @Body  body : ReceiveReturn_SerializedBody
    ) : Response<ReceiveReturnResponse>
    @POST("ReceiveReturn_Unserialized_Main")
    suspend fun ReceiveReturn_Unserialized(
        @Body  body : ReceiveReturn_UnserializedBody
    ) : Response<ReceiveReturnResponse>
    @GET("GetReturnProductionList_PutAway")
    suspend fun getReturnProductionList_PutAway(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetReturnList_PutAwayResponse>
    @GET("GetReturnReasonList")
    suspend fun getReturnReasonList(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetReturnReasonListResponse>
    @POST("PutAwayReturnProduction_Serialized")
    suspend fun PutAwayReturn_Serialized(
        @Body  body : PutAwayReturn_SerializedBody
    ) : Response<PutAwayReturnResponse>
    @POST("PutAwayReturnProduction_Unserialized")
    suspend fun PutAwayReturn_Unserialized(
        @Body  body : PutAwayReturn_UnserializedBody
    ) : Response<PutAwayReturnResponse>
    @GET("GetAuditHeader")
    suspend fun getAuditHeader(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetAuditHeaderResponse>
    @POST("SaveAuditDetails")
    suspend fun saveAuditDetails(
        @Body  body : SaveAuditDetailsBody
    ) : Response<SaveAuditDetailsResponse>
    @GET("GetMaterialByCode")
    suspend fun getMaterialByCode(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("MaterialCode")  materialCode : String,
    ) : Response<GetMaterialByCodeResponse>
    @GET("CheckSerialRelatedToMaterial")
    suspend fun checkSerialRelatedToMaterial(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("MaterialCode")  materialCode : String,
        @Query("SerialNo")  serialNo : String,
    ) : Response<NoDataResponse>
    @GET("GetPOVendorList_Receiving")
    suspend fun getPOVendorList_Receiving(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetPOVendorList_ReceivingResponse>
    @POST("ReceiveVendor_Serialized")
    suspend fun ReceiveVendor_Serialized(
        @Body  body : ReceiveVendor_SerializedBody
    ) : Response<ReceiveVendor_SerializedResponse>

    @POST("ReceiveVendor_Unserialized")
    suspend fun ReceiveVendor_Unserialized(
        @Body  body : ReceiveVendor_UnserializedBody
    ) : Response<ReceiveVendor_SerializedResponse>

    @GET("GetStock")
    suspend fun getItemInfo(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("MaterialCode")  MaterialCode : String,
    ) : Response<GetStockResponse>
    @GET("GetStock")
    suspend fun getSerialInfo(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("SerialNo")  SerialNo : String,
    ) : Response<GetStockResponse>
    @GET("GetStock")
    suspend fun getStock(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
        @Query("StorageBinCode")  StorageBinCode : String,
    ) : Response<GetStockResponse>

    @GET("GetRejectionReasonList")
    suspend fun getRejectionReasonList(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetRejectionReasonListResponse>

    @POST("ChangeSerialRequest_POFactory")
    suspend fun ChangeSerialRequest_POFactory(
        @Body  body : ChangeSerialRequest_POFactoryBody
    ) : Response<NoDataResponse>

    @POST("ChangeQuantityRequest_POFactory")
    suspend fun ChangeQuantityRequest_POFactory(
        @Body  body : ChangeQuantityRequest_POFactoryBody
    ) : Response<NoDataResponse>

    @POST("FastPutAwayInvoice")
    suspend fun FastPutAwayInvoice(
        @Body  body : FastPutAwayInvoiceBody
    ) : Response<FastPutAwayInvoiceResponse>

    @GET("GetProductionWorkOrderList_Receiving")
    suspend fun getProductionWorkOrderList_Receiving(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetProductionWorkOrderList_ReceivingResponse>

    @POST("ReceiveProductionWorkOrder_Serialized")
    suspend fun ReceiveProductionWorkOrder_Serialized (
        @Body  body : ReceiveProductionWorkOrder_SerializedBody
    ) : Response<ReceiveProductionWorkOrderResponse>

    @POST("ReceiveProductionWorkOrder_Unserialized")
    suspend fun ReceiveProductionWorkOrder_Unserialized (
        @Body  body : ReceiveProductionWorkOrder_UnserializedBody
    ) : Response<ReceiveProductionWorkOrderResponse>

    @GET("GetProductionWorkOrderList_PutAway")
    suspend fun GetProductionWorkOrderList_PutAway(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetProductionWorkOrderList_PutAwayResponse>

    @POST("PutAwayProductionWorkOrder_Serialized")
    suspend fun PutAwayProductionWorkOrder_Serialized (
        @Body  body : PutAwayProductionWorkOrder_SerializedBody
    ) : Response<PutAwayProductionWorkOrderResponse>

    @POST("PutAwayProductionWorkOrder_Unserialized")
    suspend fun PutAwayProductionWorkOrder_Unserialized (
        @Body  body : PutAwayProductionWorkOrder_UnserializedBody
    ) : Response<PutAwayProductionWorkOrderResponse>

    @GET("GetProductionWorkOrderList_Issue")
    suspend fun GetProductionWorkOrderList_Issue(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetProductionWorkOrderList_IssueResponse>

    @POST("IssueProductionWorkOrder_Serialized")
    suspend fun IssueProductionWorkOrder_Serialized (
        @Body  body : IssueProductionWorkOrder_SerializedBody
    ) : Response<IssueProductionWorkOrderResponse>

    @POST("IssueProductionWorkOrder_Unserialized")
    suspend fun IssueProductionWorkOrder_Unserialized (
        @Body  body : IssueProductionWorkOrder_UnserializedBody
    ) : Response<IssueProductionWorkOrderResponse>

    @POST("FastPutAwayProductionWorkOrder")
    suspend fun FastPutAwayProductionWorkOrder (
        @Body  body : FastPutAwayProductionWorkOrderBody
    ) : Response<NoDataResponse>

    @GET("GetReturnList_Receiving_SparePart")
    suspend fun GetReturnProductionList_Receiving(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetReturnProductionListResponse>

    @POST("ReceiveReturn_Serialized_SparePart")
    suspend fun ReceiveReturnProduction_Serialized (
        @Body  body : ReceiveReturnProduction_SerializedBody
    ) : Response<ReceiveReturnProductionResponse>

    @POST("ReceiveReturn_Unserialized_SparePart")
    suspend fun ReceiveReturnProduction_Unserialized (
        @Body  body : ReceiveReturnProduction_UnserializedBody
    ) : Response<ReceiveReturnProductionResponse>

    @GET("GetReturnList_PutAway")
    suspend fun getReturnList_PutAway(
        @Query("UserID")  userId : String,
        @Query("DeviceSerialNo")  DeviceSerialNo : String,
        @Query("applang")  appLang : String,
    ) : Response<GetReturnProductionListResponse>

    @POST("PutAwayReturn_Serialized")
    suspend fun PutAwayReturnProduction_Serialized (
        @Body  body : PutAwayReturnProduction_SerializedBody
    ) : Response<PutAwayReturnProductionResponse>

    @POST("PutAwayReturn_Unserialized")
    suspend fun PutAwayReturnProduction_Unserialized (
        @Body  body : PutAwayReturnProduction_UnserializedBody
    ) : Response<PutAwayReturnProductionResponse>

}