package ooo.emessi.messenger.managers

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.model_converter.AttachmentConverter
import ooo.emessi.messenger.data.repo.MessageRepo
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.MessageMediaConverter
import org.jivesoftware.smack.chat2.Chat
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.message_correct.element.MessageCorrectExtension
import org.jivesoftware.smackx.muclight.MultiUserChatLight
import org.jivesoftware.smackx.reference.element.ReferenceElement
import org.koin.core.KoinComponent
import org.koin.core.get
import java.io.File
import java.lang.Exception

class MessageSender(private val bzMessage: BZMessage, private val sendListener: (message: BZMessage) -> Unit): KoinComponent{

    private val messageRepo: MessageRepo = get()


    fun sendBy(chat: Chat) = CoroutineScope(Dispatchers.IO).launch{
        val message = Message(bzMessage.to.toEntityBareJid(), Message.Type.chat)
        prepareMessage(message)
        try {
            chat.send(message)
            bzMessage.isSended = true
        } catch (e: Exception){
            e.printStackTrace()
        } finally {
            sendListener.invoke(bzMessage)
        }
    }

    fun sendBy(chat: MultiUserChatLight) = CoroutineScope(Dispatchers.IO).launch {
        val message = chat.createMessage()
        prepareMessage(message)
        try {
            chat.sendMessage(message)
            bzMessage.isSended = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            sendListener.invoke(bzMessage)
        }
    }

    private fun prepareMessage(message: Message){
        message.stanzaId = bzMessage.id
        message.body = bzMessage.body
        bzMessage.messageCorrectedBody?.let {
            val correctionExtension = MessageCorrectExtension(bzMessage.id)
            message.body = bzMessage.messageCorrectedBody
            message.addExtension(correctionExtension)
        }
        bzMessage.messageReplyedId?.let { id ->
            val baseMessage = messageRepo.getMessageById(id)
            baseMessage?.let {
                val replyExtension = ReferenceElement(0, 1, ReferenceElement.Type.data, it.id, null)
                message.addExtension(replyExtension)
            }
        }
        if (bzMessage.payload.isNotBlank()) {
            val attachments = AttachmentConverter().fromJson(bzMessage)
            Log.i(this.javaClass.simpleName, attachments.toString())
            MessageMediaConverter(message).attachFilesToMessage(attachments)
            message.body = File(attachments.first().attachmentPath).name
        }

    }
}