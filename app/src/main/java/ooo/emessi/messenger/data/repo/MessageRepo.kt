package ooo.emessi.messenger.data.repo

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ooo.emessi.messenger.data.database.MessageDao
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

class MessageRepo(private val dao: MessageDao) {

    fun getMessageByChatId(chatJid: String): List<MessageDto?> {
        return dao.getMessagesByChatJid(chatJid)
    }

    fun addMessage(messageDto: MessageDto) {
        dao.insertMessage(messageDto)
    }

//    private fun createChat(message: BZMessage): BZChat {
//        val jid = message.chatJid
//        val isMulti = message.from.contains("@muclight")
//        val name = XMPPConnectionApi.getRoster().getEntry(jid.toEntityBareJid()).name
//        return BZChat(jid, name, isMulti = isMulti)
//    }

    fun getMessageById(id: String): MessageDto? {
        return dao.getMessageById(id)
    }

    fun getMessageLastInChat(chatJid: String): MessageDto? {
        return dao.getMessageLastInChat(chatJid)
    }

    fun getMessageLast(): MessageDto? {
        return dao.getMessageLast()
    }

    fun getMessages(): List<MessageDto> = dao.getAllMessages()

    fun addMessages(messageDtos: List<MessageDto>) = CoroutineScope(Dispatchers.IO).launch {
        dao.insertMessages(messageDtos)
    }

    fun delete(messageDto: MessageDto?) = CoroutineScope(Dispatchers.IO).launch {
        dao.deleteMessage(messageDto)
    }

    fun deleteMessagesByChatId(chatJid: String) = CoroutineScope(Dispatchers.IO).launch {
        dao.deleteMessagesByChatId(chatJid)
    }

    fun loadMessagesByChatId(chatId: String): LiveData<List<MessageDto>> {
        return dao.loadMessagesByChatJid(chatId)
    }

    fun loadLastMessageByChatId(chatId: String) = dao.loadLastMessageByChatId(chatId)

    fun updateMessage(messageDto: MessageDto) = CoroutineScope(Dispatchers.IO).launch {
        dao.updateMessage(messageDto)
    }
}