package net.gbs.schneider.Model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class WorkOrderReturn (

    @SerializedName("workOrderReturnId" ) var workOrderReturnId : Int?                 = null,
    @SerializedName("workOrderNumber"   ) var workOrderNumber   : String,
    @SerializedName("projectNumber"     ) var projectNumber     : String,
    @SerializedName("materials"         ) var materials         : ArrayList<MaterialReturn> = arrayListOf()

){
    companion object {
        fun toJson (invoice: WorkOrderReturn):String {
            return Gson().toJson(invoice)
        }
        fun fromJson (invoice:String):WorkOrderReturn {
            return Gson().fromJson(invoice,WorkOrderReturn::class.java)
        }
    }
}
