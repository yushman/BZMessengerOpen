package ooo.emessi.messenger.data.model.view_item_model

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.data.model.view_item_model.message.MessageViewItem

class ChatViewMaker : AbstractMaker() {
    private val messageViewMaker = MessageViewMaker()

    fun make(chatDto: ChatDto, withMessage: Boolean = false): ChatViewItem {
        val contact = if (!chatDto.isMulti) contactRepo.getContactById(chatDto.jid) else null
        var lastMessageViewItem: MessageViewItem? = null
        if (withMessage) {
            val lastMessageDto = messageRepo.getMessageLastInChat(chatDto.jid)
            lastMessageDto?.let {
                lastMessageViewItem = messageViewMaker.make(it)
            }

        }
        return ChatViewItem(chatDto, contact, lastMessageViewItem)
    }

    fun makeList(list: List<ChatDto>): List<ChatViewItem> {
        return list.map { make(it) }
    }
}