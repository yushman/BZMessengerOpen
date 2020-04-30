package ooo.emessi.messenger.xmpp.connection

import android.os.Parcel
import android.os.Parcelable

data class ConnectionState(
    var isConnected: Boolean = false,
    var isLoggedin: Boolean = false,
    var isResumed: Boolean = false,
    val domain: String? = "mossales.ru",
    var user: String? = "ggg@mossales.ru",
    var password: String? = "gggg",
    var host: String? = "mossales.ru"
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isConnected) 1 else 0)
        parcel.writeByte(if (isLoggedin) 1 else 0)
        parcel.writeByte(if (isResumed) 1 else 0)
        parcel.writeString(domain)
        parcel.writeString(user)
        parcel.writeString(password)
        parcel.writeString(host)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun isFirstLoggin() = isLoggedin && !isResumed

    companion object CREATOR : Parcelable.Creator<ConnectionState> {
        override fun createFromParcel(parcel: Parcel): ConnectionState {
            return ConnectionState(parcel)
        }

        override fun newArray(size: Int): Array<ConnectionState?> {
            return arrayOfNulls(size)
        }
    }
}