package ooo.emessi.messenger.managers.chat

import ooo.emessi.messenger.data.model.dto_model.message.MessageDto
import ooo.emessi.messenger.data.model.wrapped_model.AttachmentMessage

interface MessageSendListener {

    fun onSended(messageDto: MessageDto)
    fun onUploadResult(result: Pair<AttachmentMessage, Boolean>)
}