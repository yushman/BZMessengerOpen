package ooo.emessi.messenger.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ooo.emessi.messenger.data.model.bz_model.chat.BZChat

@Dao
interface ChatDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: BZChat)

    @Query("Select * from chats order by timeStamp desc")
    fun getAllChats(): List<BZChat>

    @Query("Select * from chats order by timeStamp desc, name desc")
    fun loadAllChats(): LiveData<List<BZChat>>

    @Query("Select * from chats where jid like :jid")
    fun loadChatById(jid: String): LiveData<BZChat>

    @Query("Select * from chats where jid like :jid")
    fun getChatById(jid: String): BZChat?

    @Transaction
    fun deleteChat(chat: BZChat){
        deleteChatDataByJid(chat.jid)
        deleteMessageByJid(chat.jid)
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
    fun insertChats(chats: List<BZChat>)

    @Update
    fun updateChat(chat: BZChat)


//    @Update
//    fun updateChat(chat: BZChat)

//    @Query("Select * from single_chats where member like :member")
//    fun selectChatByMember(member: String): BZSingleChatWithMessages




}