package net.gbs.schneider.Model

data class SerialWithStatus (
    val serial:String,
    var serialStatus: SerialStatus
)