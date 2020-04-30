package ooo.emessi.messenger.data.model.dto_model.chat

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatDto(
    @PrimaryKey
    val jid: String,
    val isMulti: Boolean = jid.contains("@muclight"),
    val name: String = jid,
    var unreadMessages: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jid)
        parcel.writeByte(if (isMulti) 1 else 0)
        parcel.writeString(name)
        parcel.writeInt(unreadMessages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatDto> {
        override fun createFromParcel(parcel: Parcel): ChatDto {
            return ChatDto(parcel)
        }

        override fun newArray(size: Int): Array<ChatDto?> {
            return arrayOfNulls(size)
        }
    }
}