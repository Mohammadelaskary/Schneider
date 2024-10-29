package net.gbs.schneider.Model.APIDataFormats.Response

import com.google.gson.annotations.SerializedName
import net.gbs.schneider.Base.BaseResponse
import net.gbs.schneider.Model.APIDataFormats.User

class SignInResponse : BaseResponse<User>() {
    @SerializedName("user"           ) var user           : User           = User()
    override fun getData(): User {
        return user
    }

}
