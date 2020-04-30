package ooo.emessi.messenger.controllers

import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.managers.chat.MucChatManager

class MUCLightChatsController (private val chatDto: ChatDto): AbstractChatController(chatDto){

    override val chatManager = MucChatManager(chatDto, this)
}