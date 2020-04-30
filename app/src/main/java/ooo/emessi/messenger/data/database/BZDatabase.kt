package ooo.emessi.messenger.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ooo.emessi.messenger.data.model.dto_model.account.AccountDto
import ooo.emessi.messenger.data.model.dto_model.chat.ChatDto
import ooo.emessi.messenger.data.model.dto_model.contact.ContactDto
import ooo.emessi.messenger.data.model.dto_model.message.MessageDto

@Database(entities = [

    //insert new entities
    AccountDto::class,
    ChatDto::class,
    MessageDto::class,
    ContactDto::class
], version = 1)

//    GOOD BD DISPATCHER
//    val dbDispatcher = CoroutineScope(Dispatchers.IO)
//    fun job(f:() -> Unit) = dbDispatcher.launch { f() }

abstract class BZDatabase : RoomDatabase() {


    //insert new dao's
    abstract fun accountDao(): AccountDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: BZDatabase? = null

        fun getInstance(context: Context): BZDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BZDatabase::class.java,
                    "BZ_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}