package ooo.emessi.messenger.ui.viewmodels

import ooo.emessi.messenger.controllers.SingleChatInfoController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class SingleChatInfoActivityViewModel (private val chatDto: ChatDto) : AbstractChatInfoViewModel(chatDto){
    override val chatInfoController = SingleChatInfoController(chatDto)

}