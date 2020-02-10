package ooo.emessi.messenger.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory(private val chatId: String) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass === ChatActivityViewModel::class.java -> ChatActivityViewModel(chatId) as T
            modelClass === MucLightChatActivityViewModel::class.java -> MucLightChatActivityViewModel(chatId) as T
            modelClass === SingleChatInfoActivityViewModel::class.java -> SingleChatInfoActivityViewModel(chatId) as T
            modelClass === MucLightChatInfoActivityViewModel::class.java -> MucLightChatInfoActivityViewModel(chatId) as T
            else -> ChatActivityViewModel(chatId) as T
        }
    }
}