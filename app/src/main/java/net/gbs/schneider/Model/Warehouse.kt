package net.gbs.schneider.Model

import com.google.gson.Gson

data class Warehouse(
    val country: String,
    val dateAdd: String,
    val dateUpdate: Any,
    val plantCode: String,
    val plantId: Int,
    val plantName: String,
    val space: String,
    val userIdAdd: Int,
    val userIdUpdate: Any,
    val userNameAdd: String,
    val userNameUpdate: Any,
    val warehouseCode: String,
    val warehouseId: Int,
    val warehouseName: String
) {
    companion object {
        fun toJson(warehouse: Warehouse):String{
            return Gson().toJson(warehouse)
        }
        fun fromJson(warehouse: String):Warehouse{
            return Gson().fromJson(warehouse,Warehouse::class.java)
        }
    }

    override fun toString(): String {
        return warehouseName
    }
}