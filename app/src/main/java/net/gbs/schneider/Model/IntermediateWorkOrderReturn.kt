package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class IntermediateWorkOrderReturn (
    @SerializedName("workOrderReturnId" ) var workOrderReturnId : Int?                 = null,
    @SerializedName("workOrderNumber"   ) var workOrderNumber   : String,
    @SerializedName("projectNumber"     ) var projectNumber     : String,
    @SerializedName("materials"         ) var materials         : List<IntermediateMaterialReturn> = listOf()

){
    companion object {
        fun toJson (invoice: IntermediateWorkOrderReturn):String {
            return Gson().toJson(invoice)
        }
        fun fromJson (invoice:String):IntermediateWorkOrderReturn {
            return Gson().fromJson(invoice,IntermediateWorkOrderReturn::class.java)
        }
    }
}