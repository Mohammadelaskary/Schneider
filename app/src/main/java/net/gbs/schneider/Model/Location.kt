package net.gbs.schneider.Model

data class Location(
    val warehouse:String,
    val storageLocation:String,
    val storageSection:String,
    val storageBin:String,
) {
    override fun toString(): String {
        return "$warehouse -> $storageLocation -> $storageSection -> $storageBin"
    }
}
