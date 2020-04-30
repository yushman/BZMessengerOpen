package ooo.emessi.messenger.data.repo

import androidx.lifecycle.LiveData
import ooo.emessi.messenger.data.database.ChatDao
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

class ChatRepo(private val dao: ChatDao) {

    fun loadChatById(chatJid: String): LiveData<ChatDto?> {
        return this.dao.loadChatById(chatJid)
    }

    fun getChatById(chatJid: String): ChatDto? {
        return this.dao.getChatById(chatJid)
    }

    fun loadChats(): LiveData<List<ChatDto>> {
        return this.dao.loadAllChats()
    }

    fun getChats(): List<ChatDto> {
        return this.dao.getAllChats()
    }

    fun addChat(chatDto: ChatDto) {
        this.dao.insertChat(chatDto)
    }

    fun deleteChat(chatDto: ChatDto) {
        this.dao.deleteChat(chatDto)
    }

    fun deleteChatById(jid: String) {
        this.dao.deleteChatByJid(jid)
    }

    fun saveChats(chatDtos: List<ChatDto>) {
        this.dao.insertChats(chatDtos)
    }

    fun updateChat(chatDto: ChatDto) {
        this.dao.updateChat(chatDto)
    }

//    fun updateChat(chat: BZChat) = GlobalScope.launch(Dispatchers.IO) {
//        dao.updateChat(chat)
//    }

//    fun getChatByMember(member: String): ChatDto {
//        return dao.selectChatByMember(member)
//    }

}