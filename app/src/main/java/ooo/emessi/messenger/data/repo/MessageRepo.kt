package ooo.emessi.messenger.data.repo

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.database.BZDatabase
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage
import ooo.emessi.messenger.managers.RosterManager
import ooo.emessi.messenger.utils.toEntityBareJid
import ooo.emessi.messenger.xmpp.XMPPConnectionApi

class MessageRepo (context: Context){
    private val dataBase = BZDatabase.getInstance(context)
    private val dao = dataBase.messageDao()
    private val chatDao = dataBase.chatDao()

    fun getMessageByChatId(chatJid: String): List<BZMessage?> {
        return dao.getMessagesByChatJid(chatJid)
    }

    fun addMessage(message: BZMessage) {
        dao.insertMessage(message)
    }

    private fun createChat(message: BZMessage): BZChat {
        val jid = message.chatJid
        val isMulti = message.from.contains("@muclight")
        val name = XMPPConnectionApi.getRoster().getEntry(jid.toEntityBareJid()).name
        return BZChat(jid, name, isMulti = isMulti)
    }

    fun getMessageById(id: String): BZMessage? {
        return dao.getMessageById(id)
    }

    fun getMessageLastInChat(chatJid: String): BZMessage? {
        return dao.getMessageLastInChat(chatJid)
    }

    fun getMessageLast(): BZMessage? {
        return dao.getMessageLast()
    }

    fun getMessages(): List<BZMessage> = dao.getAllMessages()

    fun addMessages(messages: List<BZMessage>) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertMessages(messages)
    }

    fun delete(message: BZMessage?) = CoroutineScope(Dispatchers.IO).launch {
        dao.deleteMessage(message)
    }

    fun deleteMessagesByChatId(chatJid: String) = CoroutineScope(Dispatchers.IO).launch {
        dao.deleteMessagesByChatId(chatJid)
    }

    fun loadMessagesByChatId(chatId: String): LiveData<List<BZMessage>> {
        return dao.loadMessagesByChatJid(chatId)
    }

    fun loadLastMessageByChatId(chatId: String) = dao.loadLastMessageByChatId(chatId)

    fun updateMessage(message: BZMessage) = CoroutineScope(Dispatchers.IO).launch{
        dao.updateMessage(message)
    }

}