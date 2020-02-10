package ooo.emessi.messenger.data.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.wrapped_model.ChatItem
import ooo.emessi.messenger.data.model.wrapped_model.MessageItem
import ooo.emessi.messenger.managers.ContactsManager
import ooo.emessi.messenger.managers.RosterManager

class ChatRepo (context: Context){
    private val bzDatabase = BZDatabase.getInstance(context)
    private val chatDao = bzDatabase.chatDao()
    private val contactDao = bzDatabase.contactDao()
    private val messageDao = bzDatabase.messageDao()
    private val chats: MediatorLiveData<List<ChatItem>> = MediatorLiveData()

    init {
        chats.addSource(loadChats(), Observer { toChatItem(it)})
    }

    private fun toChatItem(list: List<BZChat>?) = CoroutineScope(Dispatchers.IO).launch{
        val chatItemList = mutableListOf<ChatItem>()
        list?.forEach { bzChat ->
            var messageItem: MessageItem? = null
            bzChat.lastMessage?.let {
                val from = contactDao.selectContactById(it.from)
                messageItem = MessageItem(bzChat.lastMessage!!, from!!)
                chatItemList.add(ChatItem(bzChat, messageItem))
            }
        }
        chats.postValue(chatItemList)
    }

    fun loadChatById(chatJid: String): LiveData<BZChat> {
        return chatDao.loadChatById(chatJid)
    }

    suspend fun getChatById(chatJid: String) = withContext(Dispatchers.IO) {
        chatDao.getChatById(chatJid)
    }

    fun loadChats(): LiveData<List<BZChat>> {
        return chatDao.loadAllChats()
    }

    suspend fun getChats() = withContext(Dispatchers.IO){
        chatDao.getAllChats()
    }

    fun addChat(chat: BZChat) = CoroutineScope(Dispatchers.IO).launch {
        chatDao.insertChat(chat)
    }

    fun deleteChat(chat: BZChat) = CoroutineScope(Dispatchers.IO).launch {
        chatDao.deleteChat(chat)
    }

    fun deleteChatById(jid: String) = CoroutineScope(Dispatchers.IO).launch {
        chatDao.deleteChatByJid(jid)
    }

    fun saveChats(chats: List<BZChat>) = CoroutineScope(Dispatchers.IO).launch {
        chatDao.insertChats(chats)
    }

    fun updateChat(chat: BZChat) {
        chatDao.updateChat(chat)
    }

//    fun updateChat(chat: BZChat) = GlobalScope.launch(Dispatchers.IO) {
//        dao.updateChat(chat)
//    }

//    fun getChatByMember(member: String): ChatDto {
//        return dao.selectChatByMember(member)
//    }

}