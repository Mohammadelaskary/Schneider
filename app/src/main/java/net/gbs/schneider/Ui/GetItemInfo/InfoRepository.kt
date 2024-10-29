package net.gbs.schneider.Ui.GetItemInfo

import net.gbs.schneider.Base.BaseRepository

class InfoRepository : BaseRepository() {

    suspend fun getItemInfo(
        userId: String,
        deviceSerialNo: String,
        appLang: String,
        materialCode: String
    ) = apiInterface.getItemInfo(
        userId = userId,
        DeviceSerialNo = deviceSerialNo,
        appLang = appLang,
        MaterialCode = materialCode
    )

    suspend fun getStock(userId: String, deviceSerialNo: String, appLang: String, binCode: String) =
        apiInterface.getStock(
            userId = userId,
            DeviceSerialNo = deviceSerialNo,
            appLang = appLang,
            StorageBinCode = binCode
        )

    suspend fun getSerialInfo(
        userId: String,
        deviceSerialNo: String,
        appLang: String,
        serialCode: String
    ) = apiInterface.getSerialInfo(
        userId = userId,
        DeviceSerialNo = deviceSerialNo,
        appLang = appLang,
        SerialNo = serialCode
    )

}