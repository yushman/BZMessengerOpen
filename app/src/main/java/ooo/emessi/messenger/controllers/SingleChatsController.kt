package ooo.emessi.messenger.controllers

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.managers.chat.AbstractChatManager
import ooo.emessi.messenger.managers.chat.SingleChatManager

class SingleChatsController(private val chatDto: ChatDto) : AbstractChatController(chatDto) {
    override val chatManager: AbstractChatManager = SingleChatManager(chatDto, this)
}


