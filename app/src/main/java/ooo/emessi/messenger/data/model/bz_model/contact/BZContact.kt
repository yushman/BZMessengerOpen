package ooo.emessi.messenger.data.model.bz_model.contact

import androidx.room.Entity
import androidx.room.PrimaryKey
import ooo.emessi.messenger.data.model.wrapped_model.ContactPickItem
import ooo.emessi.messenger.utils.humanizeDiffForLastActivity
import ooo.emessi.messenger.utils.toDate
import java.util.*


@Entity(tableName = "contacts")
data class BZContact(
    @PrimaryKey
    val contactJid : String,
    val nickName : String = contactJid,
    var avatarHash: String? = null,
    var avatar : String? = null,
    var lastVisit : Long? = null,// = System.currentTimeMillis()
    var isOnline: Boolean = false,
    val isSelected: Boolean = false
){

//    var isTyping = false

    fun toWrappedContact(): ContactPickItem {
        return ContactPickItem(false, false, this)
    }

    fun getShortName(): String{
        val nick = nickName
        val fLetter = nick.first()
        val sLetter = if(!nick.contains(" ")) nick.replaceBefore(".","")[1]
        else nick.replaceBefore(" ","")[1]
//        return nick.capitalize().removeRange(2, nick.length)
        return "$fLetter$sLetter".capitalize()
    }
    //implement
    fun isSelf(jid: String) = contactJid == jid

    fun getLastActivity(): String {
        return if (isOnline) "Онлайн"
        else if (lastVisit == null) "Еще не заходил"
        else Date().humanizeDiffForLastActivity(lastVisit!!.toDate())
    }
}
