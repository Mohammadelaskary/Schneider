package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.Warehouse

class GetWarehouseListResponse(@SerializedName("getList")val warehouseList: List<Warehouse>) :
    BaseResponse<List<Warehouse>>() {
    override fun getData(): List<Warehouse> {
        return warehouseList
    }
}