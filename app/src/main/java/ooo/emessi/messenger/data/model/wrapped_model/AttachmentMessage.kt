package ooo.emessi.messenger.data.model.wrapped_model

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

data class AttachmentMessage(
    val attachment: AttachmentDto,
    val message: MessageDto
)