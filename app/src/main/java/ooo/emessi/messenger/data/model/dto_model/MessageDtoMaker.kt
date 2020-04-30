package ooo.emessi.messenger.data.model.dto_model

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.xmpp.extensions.MediaExtensionConverter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.delay.DelayInformationManager
import org.jivesoftware.smackx.reference.element.ReferenceElement
import timber.log.Timber

class MessageDtoMaker(val myJid: String) {

    private lateinit var messageDto: MessageDto
    private val attachmentDtoMaker = AttachmentDtoMaker()

    fun makeDto(message: Message): MessageDto {
        messageDto = makeBase(message)
        addExtensions(message)
        return messageDto
    }

    fun makeDto(
        chatId: String,
        body: String = "",
        attachment: AttachmentDto? = null,
        replyedMessageDto: MessageDto? = null,
        correctedMessageDto: MessageDto? = null
    ): MessageDto {
        correctedMessageDto?.let {
            return correctedMessageDto.copy(isCorrected = true, messageCorrectedBody = body)
        }
        messageDto = makeBase(chatId, body)
        addExtentions(attachment, replyedMessageDto)
        return messageDto
    }


    private fun makeBase(chatId: String, body: String) = MessageDto(
        chatId, myJid, chatId, body, false
    )

    private fun makeBase(message: Message): MessageDto {
        val chat = getChat(message)
        val from = message.from.asEntityBareJidOrThrow().asEntityBareJidString()
        val to = message.to.asEntityBareJidOrThrow().asEntityBareJidString()
        val body = message.body ?: ""
        val isIncoming = to == myJid
        return MessageDto(chat, from, to, body, isIncoming, id = message.stanzaId)
    }

    private fun getChat(message: Message): String {
        val from = message.from.asEntityBareJidOrThrow().asEntityBareJidString()
        val to = message.to.asEntityBareJidOrThrow().asEntityBareJidString()
        return when {
            from.contains("muclight") -> from
            to.contains("muclight") -> to
            from == myJid -> to
            else -> from
        }
    }

    private fun addExtentions(
        attachment: AttachmentDto?,
        replyedMessageDto: MessageDto?
    ) {
        replyedMessageDto?.let {
            messageDto = messageDto.copy(isReplyed = true)
        }
        attachment?.let {

            messageDto = messageDto.copy(
                payload = attachmentDtoMaker
                    .toJson(attachment), payloadType = attachment.calcPayloadType()
            )
        }
    }

    private fun addExtensions(message: Message) {
        if (DelayInformationManager.isDelayedStanza(message)) addDelayExtension(message)
        if (message.hasExtension("bzm:media:1")) addMediaExtension(message)
        if (ReferenceElement.containsReferences(message)) addReplyExtension(message)
    }

    private fun addDelayExtension(message: Message) {
        val delayInfo = DelayInformationManager.getDelayInformation(message)
        messageDto = messageDto.copy(timeStamp = delayInfo.stamp.time)
        Timber.i("$messageDto is delayed")
    }

    private fun addReplyExtension(message: Message) {
        val referenceElements = ReferenceElement.getReferencesFromStanza(message).first()
        messageDto = messageDto.copy(isReplyed = true, messageReplyedId = referenceElements.anchor)
        Timber.i("$messageDto is reply")
    }

    private fun addMediaExtension(message: Message) {
        val attachments = MediaExtensionConverter(message).retrievFilesFromMessage(messageDto.id)
        val payload = attachmentDtoMaker
            .toJson(attachments.first())
        val payloadType = attachments.first().calcPayloadType()
        messageDto = messageDto.copy(payload = payload, payloadType = payloadType)
        Timber.i("$messageDto is media")
    }
}