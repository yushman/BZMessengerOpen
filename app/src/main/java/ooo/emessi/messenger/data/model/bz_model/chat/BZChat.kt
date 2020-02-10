package ooo.emessi.messenger.data.model.bz_model.chat

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import ooo.emessi.messenger.utils.toEntityBareJid
import java.util.*

//@TypeConverters(ChatMembersConverter::class)
@Entity(tableName = "chats")
data class BZChat(
    @PrimaryKey
    val jid: String,
    val name: String = jid,
    @Embedded
    var lastMessage: BZMessage? = null,
    val isMulti: Boolean = jid.contains("@muclight"),
    var unreadMessages: Int = 0,
    @Embedded
    var contact: BZContact? = null
) {
    fun getNickName(): String{
        if (!isMulti && contact != null) return contact!!.nickName
        else return jid.toEntityBareJid().localpart.asUnescapedString()
    }

    fun getShortName(): String{
        val nick = name
        val fLetter = nick.first()
        val sLetter = if(!nick.contains(" ")) nick.replaceBefore(".","")[1]
        else nick.replaceBefore(" ","")[1]
//        return nick.capitalize().removeRange(2, nick.length)
        return "$fLetter$sLetter".capitalize()
    }

    fun getLastActivity(): String {
     return if (!isMulti && contact != null) contact!!.getLastActivity()
        else if (lastMessage != null) Date().humanizeDiffForLastActivity(lastMessage!!.timeStamp.toDate())
        else "Еще не заходил"
    }
}