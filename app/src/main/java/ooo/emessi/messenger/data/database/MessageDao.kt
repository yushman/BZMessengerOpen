package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

@Dao
interface MessageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(messageDto: MessageDto)

    @Query("Select * from messages where chat_id like :jid")
    fun loadMessagesByChatJid(jid: String): LiveData<List<MessageDto>>

    @Query("Select * from messages where id like :id")
    fun getMessageById(id: String): MessageDto?

    @Query("Select * from messages where chat_id like :chatId order by timeStamp desc limit 1")
    fun getMessageLastInChat(chatId: String): MessageDto?

    @Query("Select * from messages order by timeStamp desc limit 1")
    fun getMessageLast(): MessageDto?

    @Query("Select * from messages")
    fun getAllMessages(): List<MessageDto>

    @Query("Delete from messages where chat_id like :chatId")
    fun deleteMessagesByChatId(chatId: String)

    @Insert
    fun insertMessages(messageDtos: List<MessageDto>)

    @Delete
    fun deleteMessage(messageDto: MessageDto?)

    @Query("Select * from messages where chat_id like :chatJid")
    fun getMessagesByChatJid(chatJid: String): List<MessageDto>

    @Update
    fun updateMessage(messageDto: MessageDto)

    @Query("Select * from messages where chat_id like :chatId order by timeStamp desc limit 1")
    fun loadLastMessageByChatId(chatId: String): LiveData<MessageDto?>
}