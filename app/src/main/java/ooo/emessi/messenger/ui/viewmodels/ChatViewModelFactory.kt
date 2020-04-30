package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class ChatViewModelFactory(private val chat: ChatDto) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass === SingleChatActivityViewModel::class.java -> SingleChatActivityViewModel(chat) as T
            modelClass === MucLightChatActivityViewModel::class.java -> MucLightChatActivityViewModel(chat) as T
            modelClass === SingleChatInfoActivityViewModel::class.java -> SingleChatInfoActivityViewModel(chat) as T
            modelClass === MucLightChatInfoActivityViewModel::class.java -> MucLightChatInfoActivityViewModel(chat) as T
            else -> SingleChatActivityViewModel(chat) as T
        }
    }
}