package net.gbs.schneider.Model

data class Serial(
    var isPutaway: Boolean = false,
    var isReceived: Boolean = false,
    var isRejected: Boolean = false,
    val isGenerated: Boolean = false,
    val serial: String
)