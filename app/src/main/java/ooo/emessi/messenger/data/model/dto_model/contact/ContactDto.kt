package ooo.emessi.messenger.data.model.dto_model.contact

import androidx.room.Entity
import androidx.room.PrimaryKey
import ooo.emessi.messenger.utils.helpers.ColorHelper
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import java.util.*


@Entity(tableName = "contacts")
data class ContactDto(
    @PrimaryKey
    val contactJid : String,
    var name: String = contactJid,
    var avatarHash: String? = null,
    var avatar : String? = null,
    var lastActivity : Long? = null,
    var isOnline: Boolean = false,
    var isMe: Boolean = false
){
    fun getShortName(): String{
        val nick = name
        val fLetter = nick.first()
        val sLetter = if(!nick.contains(" ")) nick.replaceBeforeLast("@","")[1]
        else nick.replaceBeforeLast(" ","")[1]
        return "$fLetter$sLetter".capitalize()
    }

    fun isSelf(jid: String) = contactJid == jid

    fun getLastActivityInfo(): String {
        return when {
            isOnline -> "Онлайн"
            lastActivity == null -> "Еще не заходил"
            else -> Date().humanizeDiffForLastActivity(lastActivity!!.toDate())
        }
    }

    fun getColor(): Int{
        val colorGen = ColorHelper.MATERIAL
        return colorGen.getColor(this.contactJid)
    }
}
