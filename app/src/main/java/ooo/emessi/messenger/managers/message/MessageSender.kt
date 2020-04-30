package ooo.emessi.messenger.managers.message

import ooo.emessi.messenger.data.model.dto_model.AttachmentDtoMaker
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPApi
import ooo.emessi.messenger.xmpp.chat.AbstractXChat
import ooo.emessi.messenger.xmpp.extensions.MediaExtensionConverter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.message_correct.element.MessageCorrectExtension
import org.jivesoftware.smackx.reference.element.ReferenceElement
import org.koin.core.KoinComponent
import org.koin.core.get

class MessageSender(private var messageDto: MessageDto, private val xChat: AbstractXChat) :
    KoinComponent {

    private val xmppApi = get<XMPPApi>()

    fun send(sendListener: (messageDto: MessageDto) -> Unit) {
        val message = Message(messageDto.to.toEntityBareJid())
        prepareMessage(message)
        try {
            xmppApi.send(message, xChat)
            messageDto = messageDto.copy(isSended = true)
        } catch (e: Exception){
            e.printStackTrace()
            messageDto = messageDto.copy(isSendingError = true)
        } finally {
            sendListener.invoke(messageDto)
        }
    }

    private fun prepareMessage(message: Message){
        message.stanzaId = messageDto.id
        message.body = messageDto.body
        messageDto.messageCorrectedBody?.let {
            val correctionExtension = MessageCorrectExtension(messageDto.id)
            message.body = messageDto.messageCorrectedBody
            message.addExtension(correctionExtension)
        }
        messageDto.messageReplyedId?.let { id ->
            val replyExtension = ReferenceElement(0, 1, ReferenceElement.Type.data, id, null)
            message.addExtension(replyExtension)
        }
        if (messageDto.payload.isNotBlank()) {
            val attachment = AttachmentDtoMaker()
                .fromJson(messageDto)
            MediaExtensionConverter(message).attachFilesToMessage(attachment!!)
        }
    }
}