package net.gbs.schneider.Model

import net.gbs.schneider.Tools.Status

data class StatusWithMessage(
    val status:Status,
    val message:String = "",
)
