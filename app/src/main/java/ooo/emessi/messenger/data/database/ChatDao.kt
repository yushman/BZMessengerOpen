package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto

@Dao
interface ChatDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chatDto: ChatDto)

    @Query("Select * from chats") // order by timeStamp desc
    fun getAllChats(): List<ChatDto>

    @Query("Select * from chats") // order by timeStamp desc
    fun loadAllChats(): LiveData<List<ChatDto>>

    @Query("Select * from chats where jid like :jid")
    fun loadChatById(jid: String): LiveData<ChatDto?>

    @Query("Select * from chats where jid like :jid")
    fun getChatById(jid: String): ChatDto?

    @Transaction
    fun deleteChat(chatDto: ChatDto) {
        deleteChatDataByJid(chatDto.jid)
        deleteMessageByJid(chatDto.jid)
    }

    @Transaction
    fun deleteChatByJid(jid: String) {
        deleteChatDataByJid(jid)
        deleteMessageByJid(jid)
    }

    @Query("Delete from chats where jid like :jid")
    fun deleteChatDataByJid(jid: String)

    @Query("Delete from messages where chat_id like :jid")
    fun deleteMessageByJid(jid: String)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertChats(chatDtos: List<ChatDto>)

    @Update
    fun updateChat(chatDto: ChatDto)


//    @Update
//    fun updateChat(chat: BZChat)

//    @Query("Select * from single_chats where member like :member")
//    fun selectChatByMember(member: String): BZSingleChatWithMessages




}