package net.gbs.schneider.Ui.Audit

import net.gbs.schneider.Base.BaseRepository

class AuditRepository : BaseRepository() {

    suspend fun getAuditHeader(userId: String, deviceSerialNo: String, appLang: String) =
        apiInterface.getAuditHeader(userId, deviceSerialNo, appLang)

}