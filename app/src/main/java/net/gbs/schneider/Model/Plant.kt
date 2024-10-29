package net.gbs.schneider.Model

import com.google.gson.Gson

data class Plant(
    val country: String,
    val dateAdd: String,
    val dateUpdate: Any,
    val plantCode: String,
    val plantId: Int,
    val plantName: String,
    val userIdAdd: Int,
    val userIdUpdate: Any,
    val userNameAdd: String,
    val userNameUpdate: Any
) {
    companion object {
        fun toJson(plant: Plant):String{
            return Gson().toJson(plant)
        }
        fun fromJson(plant: String):Plant{
            return Gson().fromJson(plant,Plant::class.java)
        }
    }

    override fun toString(): String {
        return plantName
    }
}