package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.MUCLightChatsController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class MucLightChatActivityViewModel(private val chatDto: ChatDto) : AbstractChatViewModel(chatDto){

    override val controller = MUCLightChatsController(chatDto)
}