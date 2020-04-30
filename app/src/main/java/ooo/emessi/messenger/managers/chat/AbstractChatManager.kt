package ooo.emessi.messenger.managers.chat

import ooo.emessi.messenger.data.model.dto_model.MessageDtoMaker
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.wrapped_model.AttachmentMessage
import ooo.emessi.messenger.managers.AbstractManager
import ooo.emessi.messenger.managers.attachment.AttachmentManager
import ooo.emessi.messenger.managers.message.MessageSender
import ooo.emessi.messenger.xmpp.XMPPApi
import ooo.emessi.messenger.xmpp.chat.AbstractXChat
import org.koin.core.get

abstract class AbstractChatManager(
    private val chatDto: ChatDto,
    private val listener: MessageSendListener?
) : AbstractManager() {
    protected val xmppApi = get<XMPPApi>()
    protected val messageDtoMaker = MessageDtoMaker(xmppApi.getMyJid().asEntityBareJidString())

    abstract val xChat: AbstractXChat

    fun fetchMessages() = messageRepo.loadMessagesByChatId(chatDto.jid)

    fun createChat() = chatRepo.addChat(chatDto)

    fun deleteChat() {
        xmppApi.leaveChat(xChat)
        messageRepo.deleteMessagesByChatId(chatDto.jid)
        chatRepo.deleteChat(chatDto)
    }

    fun clearChatHistory() {
        messageRepo.deleteMessagesByChatId(chatDto.jid)
    }

    fun sendMessage(message: MessageDto) {
        MessageSender(message, xChat).send { handleMessageSended(it) }
    }

    fun sendMessage(
        body: String,
        replyedMessageDto: MessageDto?,
        correctedMessageDto: MessageDto?
    ) {
        val message =
            messageDtoMaker.makeDto(chatDto.jid, body, null, replyedMessageDto, correctedMessageDto)
        sendMessage(message)
    }

    fun sendAttachments(attachmentsPaths: List<String>, replyedMessageDto: MessageDto? = null) {
        if (attachmentsPaths.isEmpty()) return
        val attachmentManager = get<AttachmentManager>()
        val attachments = attachmentManager.createAttachments(attachmentsPaths)
        val attachmentMessages = attachments.map {
            AttachmentMessage(
                it,
                messageDtoMaker.makeDto(chatDto.jid, attachment = it)
            )
        }
            .toMutableList()
        replyedMessageDto?.let {
            val firstMessage =
                attachmentMessages.first().message.copy(messageReplyedId = it.messageReplyedId)
            attachmentMessages.set(0, attachmentMessages.first().copy(message = firstMessage))
        }
        messageRepo.addMessages(attachmentMessages.map { it.message })
        attachmentManager.uploadAttachments(attachmentMessages) { handleUpload(it) }
    }

    fun flushChatUnread() {
        chatDto.unreadMessages = 0
        chatRepo.updateChat(chatDto)
    }

    fun deleteMessage(message: MessageDto) {
        messageRepo.delete(message)
    }

    fun fetchChat() = chatRepo.loadChatById(chatDto.jid)

    private fun handleMessageSended(message: MessageDto) {
        messageRepo.updateMessage(message)
        listener?.onSended(message)
    }

    private fun handleUpload(result: Pair<AttachmentMessage, Boolean>) {
        if (result.second) sendMessage(result.first.message)
        listener?.onUploadResult(result)
    }
}