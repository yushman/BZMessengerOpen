package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.SingleChatsController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class SingleChatActivityViewModel(chatDto: ChatDto) : AbstractChatViewModel(chatDto) {
    override val controller = SingleChatsController(chatDto)
}