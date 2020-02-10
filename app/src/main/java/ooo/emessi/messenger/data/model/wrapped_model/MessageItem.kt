package ooo.emessi.messenger.data.model.wrapped_model

import ooo.emessi.messenger.data.model.bz_model.attachment.ABZAttachment
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.contact.BZContact
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage

data class MessageItem(
    val message: BZMessage,
    val from: BZContact? = null,
    val payload: List<ABZAttachment> = listOf(),
    val replyedMessage: BZMessage? = null
){
    fun toBZMessage() = message

    fun getContactName(): String {
        return from?.nickName ?: message.from
    }

    fun getShortName(): String{
        val nick = getContactName()
        val fLetter = nick.first()
        val sLetter = if(!nick.contains(" ")) nick.replaceBefore(".","")[1]
        else nick.replaceBefore(" ","")[1]
//        return nick.capitalize().removeRange(2, nick.length)
        return "$fLetter$sLetter".capitalize()
    }
}