package ooo.emessi.messenger.controllers

import androidx.lifecycle.MutableLiveData
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.view_item_model.ChatViewMaker
import ooo.emessi.messenger.data.model.view_item_model.chat.ChatViewItem
import ooo.emessi.messenger.managers.chat.AbstractChatManager

abstract class AbstractChatInfoController(private val chatDto: ChatDto){
    abstract val chatManager:AbstractChatManager
    val chatViewItem: MutableLiveData<ChatViewItem> = MutableLiveData()

    fun loadChatInfo(){
        chatViewItem.postValue(cookChatItem())
    }

    fun leaveChat() {
        chatManager.deleteChat()
    }

    private fun cookChatItem() = ChatViewMaker().make(chatDto)
}