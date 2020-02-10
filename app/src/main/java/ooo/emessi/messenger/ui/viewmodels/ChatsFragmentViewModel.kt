package ooo.emessi.messenger.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.data.repo.ChatRepo
import ooo.emessi.messenger.controllers.ChatListController
import org.koin.core.KoinComponent
import org.koin.core.get

class ChatsFragmentViewModel : ViewModel(), KoinComponent{
    private val TAG = this.javaClass.simpleName

    private val chatListManager =
        ChatListController()
    private val chatRepo: ChatRepo = get()

//    val chats: LiveData<List<BZChat>> = chatListManager.chats
    val chats = chatListManager.chats
    val forwardedMessage: LiveData<BZMessage> = chatListManager.forwardedMessage
    var isForwarded = false

    fun loadMam() {
//        ChatListManager.loadMessagesFromMam()
    }

    fun loadChatsFromRoster() {
        chatListManager.loadChatsFromRoster()
        Log.d(TAG, "load chats from roster")
    }

//    fun filterChats(query: String) =  Transformations.map(chats){chats -> chats.filter { it.name.contains(query) }}.value

    fun updateChatsFromRoster() {
        chatListManager.updateChatsFromRoster()
    }

    fun loadForwardedMessage(forwardedMessage: String) {
        chatListManager.loadForwardedMessage(forwardedMessage)
    }
    fun forwardMessage(it: BZChat){
        chatListManager.forwardMessage(it)
        isForwarded = false
    }

    fun exitFromAccount() {

    }


}