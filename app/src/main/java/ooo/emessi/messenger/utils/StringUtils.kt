package ooo.emessi.messenger.utils

import org.bouncycastle.util.encoders.Hex

fun String.splitUserHost(): Pair<String, String>{
    val split = this.split("@")
    return if (split.size == 1) split[0] to ""
    else split[0] to split[1]
}

fun String.getUser(): String{
    return this.splitUserHost().first
}

fun String.getHost(): String{
    return this.splitUserHost().second
}

fun String.parseFileName(): String {
    val split = this.split("/")
    return split.last()
}

fun String.decodeHex(): ByteArray{
    return Hex.decode(this)
}

fun ByteArray.encodeStringHex(): String{
    return Hex.toHexString(this)
}

fun String.isMultiChat(): Boolean = this.contains("muclight")