package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.message.BZMessage

@Dao
interface MessageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: BZMessage)

    @Query("Select * from messages where chat_id like :jid")
    fun loadMessagesByChatJid(jid: String): LiveData<List<BZMessage>>

    @Query("Select * from messages where id like :id")
    fun getMessageById(id: String): BZMessage?

    @Query("Select * from messages where chat_id like :chatId order by timeStamp desc limit 1")
    fun getMessageLastInChat(chatId: String): BZMessage?

    @Query("Select * from messages order by timeStamp desc limit 1")
    fun getMessageLast(): BZMessage?

    @Query("Select * from messages")
    fun getAllMessages(): List<BZMessage>

    @Query("Delete from messages where chat_id like :chatId")
    fun deleteMessagesByChatId(chatId: String)

    @Insert
    fun insertMessages(messages: List<BZMessage>)

    @Delete
    fun deleteMessage(message: BZMessage?)

    @Query("Select * from messages where chat_id like :chatJid")
    fun getMessagesByChatJid(chatJid: String): List<BZMessage>

    @Update
    fun updateMessage(message: BZMessage)

    @Query("Select * from messages where chat_id like :chatId order by timeStamp desc limit 1")
    fun loadLastMessageByChatId(chatId: String): LiveData<BZMessage?>
}