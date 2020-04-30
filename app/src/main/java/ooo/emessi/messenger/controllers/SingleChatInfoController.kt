package ooo.emessi.messenger.controllers

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.managers.chat.SingleChatManager

class SingleChatInfoController(private val chatDto: ChatDto): AbstractChatInfoController(chatDto){
    override val chatManager = SingleChatManager(chatDto)
}