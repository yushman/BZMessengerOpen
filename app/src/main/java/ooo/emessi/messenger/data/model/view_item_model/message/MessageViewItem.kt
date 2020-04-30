package ooo.emessi.messenger.data.model.view_item_model.message

import ooo.emessi.messenger.data.model.dto_model.attachment.AttachmentDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto


data class MessageViewItem(
    val body: String,
    val messageDto: MessageDto,
    val fromContactDto: ContactDto?,
    val payload: AttachmentDto?,
    val replyedMessage: MessageViewItem?
) {
    fun getContactName() = fromContactDto?.name ?: messageDto.from
}