package ooo.emessi.messenger.data.model.view_item_model.chat

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto

data class ChatForwardViewItem(
    val chatDto: ChatDto,
    var contactDto: ContactDto?
)