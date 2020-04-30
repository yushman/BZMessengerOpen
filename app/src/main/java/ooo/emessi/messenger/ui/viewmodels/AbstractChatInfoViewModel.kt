package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.controllers.AbstractChatInfoController
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

abstract class AbstractChatInfoViewModel(private val chatDto: ChatDto): ViewModel(){
    abstract val chatInfoController: AbstractChatInfoController
    val chatViewItem = chatInfoController.chatViewItem

    fun loadChatInfo(){
        chatInfoController.loadChatInfo()
    }

    fun leaveChat(){
        chatInfoController.leaveChat()
    }
}